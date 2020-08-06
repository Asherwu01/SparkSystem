package com.asher.spark.tutorial.core_project.app

import com.asher.spark.tutorial.core_project.base.BaseApp
import org.apache.spark.rdd.RDD

/**
 * Created by VULCAN on 2020/7/17
 *
 *      逻辑分析：
 *               Hive表：   Data(
                             * date: String,//用户点击行为的日期
                             * user_id: Long,//用户的ID
                             * session_id: String,//Session的ID
                             * page_id: Long,//某个页面的ID
                             * action_time: String,//动作的时间点
                             * search_keyword: String,//用户搜索的关键词
                             * click_category_id: Long,//某一个商品品类的ID
                             * click_product_id: Long,//某一个商品的ID
                             * order_category_ids: String,//一次订单中所有品类的ID集合
                             * order_product_ids: String,//一次订单中所有商品的ID集合
                             * pay_category_ids: String,//一次支付中所有品类的ID集合
                             * pay_product_ids: String,//一次支付中所有商品的ID集合
                             * city_id: Long
 *
 *                           )
 *
 *                      一条数据只会记录一种行为！
 *
 *                 SQL：
 *                        点击数：
 *                              (select  click_category_id category_id,count(*)  clickCount
 *                              from data
 *                              where click_category_id != -1
 *                              group by  click_category_id ) t2
 *                        下单数：
 *                              (select  category_id, count(*) orderCount
 *                              from
 *                                (select  category_id
 * *                              from data
 * *                              where order_category_ids is not null
 *                                lateral view explode(order_category_ids) tmp as category_id) t1
 * *                             group by  category_id ) t3
 *
 *                        支付数：
 *                               ( select  category_id, count(*) payCount
 * *                              from
 * *                                (select  category_id
 * * *                              from data
 * * *                              where pay_category_ids is not null
 * *                                lateral view explode(pay_category_ids) tmp as category_id) t4
 * * *                             group by  category_id ) t5
 *
 *                          Join:  select
 *                                  from t1
 *                                  leftjoin t3 on t1.category_id=t3.category_id
 *                                  leftjoin  t5 on t1.category_id=t5.category_id
 *
 *
 *                          避免Join?    Join  使用   union all  再 group sum
 *
 *                         思路二：
 *                          select category_id,sum(clickCount) clickCount,
 *                                sum(orderCount) orderCount,
 *                                sum(payCount) payCount
 *                          from
 *                              select  category_id,count(*)  clickCount, 0 orderCount, 0 payCount
 * *                              from data
 * *                              where click_category_id != -1
 * *                              group by  click_category_id
 *
 *                              union all
 *
 *                               select  category_id,  0   clickCount, count(*) orderCount, 0 payCount
 *                              union all
 *
 *                                 select  category_id,  0   clickCount, 0 orderCount, count(*) payCount
 *                           group by category_id
 *
 *
 *
 *
 *
 *
 *
 *
 *              结果：   (品类  点击数   下单数  支付数)
 *
 */
object Top10HotCategoryApp extends BaseApp{

  override val outPutPath: String = "output/top10hot"

  def main(args: Array[String]): Unit = {

    runApp{

      // 统计点击数
      val datas: RDD[String] = sc.textFile("datas/user_visit_action.txt")

      //过滤 where
      val filterData: RDD[String] = datas.filter(line => {
        val words: Array[String] = line.split("_")
        words(6) != "-1"
      })

      // 在分组前将数据变为 (catagory,1)

      val mapData: RDD[(String, Int)] = filterData.map(line => {
        val words: Array[String] = line.split("_")
        (words(6), 1)
      })

      // groupBy：  (品类,点击数)
      val ClickResult: RDD[(String, Int)] = mapData.reduceByKey(_ + _)

      //统计下单数

      // 只过滤下单的数据
      val filterDatas4Order: RDD[String] = datas.filter(line => {

        val words: Array[String] = line.split("_")

        words(8) != "null"

      })

      // 将下单的每行数据转换为 (品类，1)
      val orderDatas: RDD[(String, Int)] = filterDatas4Order.flatMap(line => {

        val words: Array[String] = line.split("_")

        val catagerys: Array[String] = words(8).split(",")

        for (catagery <- catagerys) yield (catagery, 1)

      })

      // 将下单的数据按照类别累加：   (品类，1)....  =>   (品类，N)
      val orderResult: RDD[(String, Int)] = orderDatas.reduceByKey(_ + _)


      println("---------------------支付数据----------------")

      // 只过滤下单的数据
      val filterDatas4Pay: RDD[String] = datas.filter(line => {

        val words: Array[String] = line.split("_")

        words(10) != "null"

      })

      // 将下单的每行数据转换为 (品类，1)
      val payDatas: RDD[(String, Int)] = filterDatas4Pay.flatMap(line => {

        val words: Array[String] = line.split("_")

        val catagerys: Array[String] = words(10).split(",")

        for (catagery <- catagerys) yield (catagery, 1)

      })

      // 将下单的数据按照类别累加：   (品类，1)....  =>   (品类，N)
      val payResult: RDD[(String, Int)] = payDatas.reduceByKey(_ + _)

      // 合并支付数据 和 下单数据 和点击数据为   (品类，点击 ，下单 , 支付)
      val joinDatas: RDD[(String, ((Int, Option[Int]), Option[Int]))] = ClickResult.leftOuterJoin(orderResult).leftOuterJoin(payResult)

      val conversionJoinDatas: RDD[(String, (Int, Int, Int))] = joinDatas.map {

        case (category, ((clickCount, orderCount), payCount)) => (category, (clickCount, orderCount.getOrElse(0), payCount.getOrElse(0)))

      }

      // 未排序的数据
      val result: List[(String, (Int, Int, Int))] = conversionJoinDatas.collect().toList

      // top10
      val top10Caterogy: List[(String, (Int, Int, Int))] = result.sortBy(x => x._2)(
        Ordering.Tuple3(Ordering.Int.reverse, Ordering.Int.reverse, Ordering.Int.reverse)).take(10)

      sc.makeRDD(top10Caterogy,1).saveAsTextFile(outPutPath)





    }


  }
}
