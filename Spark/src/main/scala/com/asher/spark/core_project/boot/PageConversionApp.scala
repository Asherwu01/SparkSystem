package com.asher.spark.core_project.boot

import java.text.DecimalFormat

import com.asher.spark.core_project.base.BaseApp
import com.asher.spark.core_project.bean.UserVisitAction
import org.apache.spark.rdd.RDD

/**
 * Created by VULCAN on 2020/7/18
 *
 * 页面单跳转换率统计:   一个页面的单向跳转 转换率统计
 *
 *      首页 ------> 搜索 ----->  点击 --------> 加入购物车 -------->下单 --------->支付
 *
 *      100w-------> 80w ----->  50w  --------> 30w--------------->5w  ----------> 1w
 *
 *      单向跳转 : 直接跳转  ：         首页 ------> 搜索
 *                 间接调转  ：        首页  ----->  点击
 *
 *      对象： 页面的单向跳转转换率
 *                A--->B的单向跳转转换率 =  访问A-B页面的人数  /  访问A页面的人数
 *
 *
 *      思路：    一条代表 一个session 对 一个页面的访问记录
 *                    如果要统计一个session 对页面的跳转链 ：
 *                              ①统计某个 单向跳转的总次数
 *                                  A-B 单向跳转的总次数  ：
 *                                        a)  按照session分组， 按照对页面的访问的时间顺序，升序排列，组成当前session对页面的
 *                                            访问顺序链
 *                                                session1 :  A-B-C-D-E
 *                                                session2 :  A-B
 *                                                session3 :  A-B-C
 *                                                session4 :  A-B-C-D
 *                                        b)  将每个Session的访问顺序链两两结合，
 *                                                    session1 :  A-B-C-D-E （A-B） （B-C） （C-D） （D-E）
 *                                                    session2 :  A-B         （A-B）
 *                                                    session3 :  A-B-C         （A-B） （B-C）
 *                                                    session4 :  A-B-C-D      （A-B） （B-C） （C-D）
 *                                        c)统计单向跳转的总次数
 *                                                  （A-B）,4
 *                                                   B-C）,3
 *                                                   ...
 *
 *                              ②统计页面单向跳转的总次数的概率
 *                                  从原始数据，对每个页面的访问次数进行统计：
 *                                        (A-20)
 *
 *                              ③统计概率
 *                                        A-B）,4 /   (A-20)
 *                                        4/20
 *
 *
 */
object PageConversionApp extends  BaseApp{
  override var outputPath: String = "Spark/project_output/PageConversionApp"
  def getAllBeans() = {
    val data = sc.textFile("Spark\\core_project_input\\user_visit_action.txt")
    val beans = data.map(line => {
      val words = line.split("_")
      UserVisitAction(
        words(0),
        words(1).toLong,
        words(2),
        words(3).toLong,
        words(4),
        words(5),
        words(6).toLong,
        words(7).toLong,
        words(8),
        words(9),
        words(10),
        words(11),
        words(12).toLong,
      )
    })
    beans
  }
  def main(args: Array[String]): Unit = {

    runApp{

      //对每个页面的访问次数进行统计
      val sourcRDD: RDD[UserVisitAction] = getAllBeans()

      val rdd1: RDD[(Long, Int)] = sourcRDD.map(bean => (bean.page_id, 1))

      // 每个页面访问的总次数
      val rdd2: RDD[(Long, Int)] = rdd1.reduceByKey(_ + _)

      // 每个页面访问的总次数
      val pageCounts: Map[Long, Int] = rdd2.collect().toMap


      //---------------------------------------------------------

      // 取 (sessionid,(页面，时间))
      val rdd3: RDD[(String, (Long, String))] = sourcRDD.map(bean => (bean.session_id, (bean.page_id, bean.action_time)))

      val rdd4: RDD[(String, Iterable[(Long, String)])] = rdd3.groupByKey()

      //有序的
      val rdd5: RDD[(String, List[(Long, String)])] = rdd4.mapValues(it => {
        // 默认按照时间升序排
        it.toList.sortBy(x => x._2)
      })

      val rdd6: RDD[List[(Long, String)]] = rdd5.values

      // RDD[List(A,B,C,D),List(B,C,D)...]
      val rdd7: RDD[List[Long]] = rdd6.map(list => {
        list.map(x => x._1)
      })

      // 页面两两组合
      val rdd8: RDD[List[(Long, Long)]] = rdd7.map(list => {
        // A,B,C,D  =>  (A,B) (B,C) (C,D)
        list.zip(list.tail)
      })

      //  页面两两组合
      val rdd9: RDD[(Long, Long)] = rdd8.flatMap(list => list)

      //  页面两两组合,及访问的总次数
      val rdd10: RDD[((Long, Long), Int)] = rdd9.map(x => (x, 1)).reduceByKey(_ + _,1)

      //------------------------------计算概率-----------------------

      // 创建格式
      val format = new DecimalFormat(".00%")

      val result: RDD[String] = rdd10.map {

        case ((fromPage, toPage), count) => fromPage+"-"+toPage+"="+format.format(count.toDouble / pageCounts.getOrElse(fromPage, 1))

      }

      result.saveAsTextFile(outputPath)






    }

  }
}
