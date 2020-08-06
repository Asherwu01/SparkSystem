package com.asher.spark.core_project.boot

import com.asher.spark.core_project.base.BaseApp
import org.apache.spark.rdd.RDD

/*
//用户访问动作表 Hive表
case class UserVisitAction(
    date: String,//用户点击行为的日期
    user_id: Long,//用户的ID
    session_id: String,//Session的ID
    page_id: Long,//某个页面的ID
    action_time: String,//动作的时间点
    search_keyword: String,//用户搜索的关键词
    click_category_id: Long,//某一个商品品类的ID  7
    click_product_id: Long,//某一个商品的ID  8
    order_category_ids: String,//一次订单中所有品类的ID集合
    order_product_ids: String,//一次订单中所有商品的ID集合
    pay_category_ids: String,//一次支付中所有品类的ID集合
    pay_product_ids: String,//一次支付中所有商品的ID集合
    city_id: Long
)//城市 id

需求一：
  分别统计每个品类点击的次数，下单的次数和支付的次数，并求热门品类的top10

分析：
  一条记录只会记录一种行为，如用户搜索，那么属于 搜索 信息，用户在搜索时不会进行 点击 或者 下单 或者 支付，每种行为都是独立的作为一条记录。
  热度统计指标：先按点击次数，如果相同，再按下单次数，如果还相同再按支付次数

sql实现：

   点击数
   (select category_id, count(*) clickCount
   from data
   where click_category_id != -1
   group by click_category_id) t1

   下单数
   (select category_id,count(*) orderCount
   from
   (select category_id, count(*) orderCount
   from data
   where order_category_ids is not null
   lateral view explode(order_category_ids) tmp as category_id) t2
   group by category_id) t3

   支付数
   (select category_id,count(*) payCount
   from
   (select category_id, count(*) orderCount
   from data
   where pay_category_ids is not null
   lateral view explode(pay_category_ids) tmp as category_id) t4
   group by category_id) t5

   结果：进行join得到结果
   select
   from t1 left join t3 on t1.category_id = t3.category_id
   from t1 left join t5 on t1.category_id = t5.category_id

sql实现，对join改进，使用 union all group sum
点击数
    select category_id,sum(clickCount),sum(orderCount),sum(payCount)
    from
   (select category_id, count(*) clickCount,0,0
   from data
   where click_category_id != -1
   group by click_category_id) t1

   union all
   (select category_id,0,count(*) orderCount,0
   from
   (select category_id, count(*) orderCount
   from data
   where order_category_ids is not null
   lateral view explode(order_category_ids) tmp as category_id) t2
   group by category_id) t3

   union all
   (select category_id,0,0,count(*) payCount
   from
   (select category_id, count(*) orderCount
   from data
   where pay_category_ids is not null
   lateral view explode(pay_category_ids) tmp as category_id) t4
   group by category_id) t5
   group by category_id

 */
object Top10HotCategoryApp extends BaseApp {
  override var outputPath: String = "Spark/project_output/Top10HotCategoryApp"

  def main(args: Array[String]): Unit = {
    runApp {
      //原始数据
      val data = sc.textFile("Spark/core_project_input")

      // 统计点击数
      //过滤
      val filterClickData = data.filter(line => {
        val words = line.split("_")
        words(6) != "-1"

      })

      //将数据转换成（点击品类，1）
      val clickData = filterClickData.map(line => {
        (line.split("_")(6), 1)
      })

      val clickResult = clickData.reduceByKey(_ + _)


      // 统计下单数
      //过滤
      val filterOrderData = data.filter(line => {
        val words = line.split("_")
        words(8) != "null"

      })
      //将数据转换成（下单品类，1）
      val orderData = filterOrderData.flatMap(line => {
        val categorys = line.split("_")(8)
        val words = categorys.split(",")
        for (word <- words) yield word -> 1
      })

      //累加下单数据
      val orderResult = orderData.reduceByKey(_ + _)

      // 统计支付数
      // 过滤
      val filterPayData = data.filter(line => {
        val words = line.split("_")
        words(10) != "null"

      })
      //将数据转换成（支付品类，1）
      val payData = filterOrderData.flatMap(line => {
        val payCategorys = line.split("_")(8)
        val categorys = payCategorys.split(",")
        for (category <- categorys) yield category -> 1
      })

      //累加下单数据
      val payResult = payData.reduceByKey(_ + _)

      //合并点击，下单，支付 (品类，点击，下单，支付)
      val joinData: RDD[(String, ((Int, Option[Int]), Option[Int]))] = clickResult.leftOuterJoin(orderResult).leftOuterJoin(payResult)
      val converseData = joinData.map {
        case (category, ((clickCount, orderCount), payCount)) =>
          (category, (clickCount, orderCount.getOrElse(0), payCount.getOrElse(0)))
      }
      val listData = converseData.collect.toList
      val top10Category = listData.sortBy(_._2)(Ordering.Tuple3(Ordering.Int.reverse, Ordering.Int.reverse, Ordering.Int.reverse)).take(10)

      sc.makeRDD(top10Category,1).saveAsTextFile(outputPath)
    }
  }

}
