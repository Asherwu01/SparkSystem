package com.asher.spark.core_project.boot

import com.asher.spark.core_project.base.BaseApp
import com.asher.spark.core_project.bean.UserVisitAction
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

/**
 * @Author Asher Wu
 * @Date 2020/8/6 9:28
 * @Version 1.0
 *
 *   需求：在需求一的基础上，增加每个品类用户session的点击统计
 *    数据：一条数据代表一个session对商品的搜索或某个品类的点击、下单、支付的访问。
 *    Top10热门品类中，每个品类的Top10活跃Session统计，即点击数最多的10个Session
 *    1.获取统一的Top10热门品类
 *    2.进一步统计这10个品类，每个品类的Top10Session
 *      a.先过滤出Top10类的所有数据
 *      b.再过滤出TOp10类的点击数据
 *      c.按照 类别id-sessionId 对以上过滤数据进行分组。
 *      d.统计每个类别下，每个session在数据中一共点击了多少次， 类别id-sessionId 10
 *      e.将数据进行转换，类别id，sessionid-10,按照类别id分组，类别(id，List(sessionid1-N,session2-N)...)
 *      f.按次数进行排序，求前十 {
 *                              (类别1，List(sessionid1-n1,sessionid2-n2,...sessionid10-n10)),
 *                              (类别2，List(sessionid1-n1,sessionid2-n2,...sessionid10-n10))
 *                              ...
 *                              (类别10，List(sessionid1-n1,sessionid2-n2,...sessionid10-n10))
 *                            }
 */
object Top10HotCategoryTop10SessionApp extends BaseApp{
  override var outputPath: String = "Spark/project_output/Top10HotCategoryTop10SessionApp"

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
      // 1.获取需求一统计的top10 热度的 品类
      val rdd1: RDD[String] = sc.textFile("Spark\\project_output\\Top10HotCategoryAccApp\\part-00000")

      val top10Catagorys: List[String] = rdd1.collect().toList

      // top10热度的类别  只读
      val top10CategoryNames: List[String] = top10Catagorys.map(line => line.split(",")(0))


      //放入广播变量
      val top10CategoryNamesBc: Broadcast[List[String]] = sc.broadcast(top10CategoryNames)

      //a) 先过滤出top10类型的所有数据 ,  b) 再过滤出 top10类别的点击数据
      val beans: RDD[UserVisitAction] = getAllBeans()


      // 在map中过滤，过滤后，复合要求的数据，封装    List[  ((Long, String), Int),((Long, String), Int),((Long, String), Int)           ]
      val rdd2: RDD[((Long, String), Int)] = beans.flatMap(bean => {

        if (bean.click_category_id != -1 && top10CategoryNamesBc.value.contains(bean.click_category_id+"")) {

          List(((bean.click_category_id, bean.session_id), 1))

        } else {

          Nil

        }

      })

      //rdd2.collect().foreach(println)


      // 根据(品类ID, sessionID) 聚合，统计每个品类下每个session点击了多少次
      val rdd3: RDD[((Long, String), Int)] = rdd2.reduceByKey(_ + _)


      // 完成格式的转换   ((品类ID, sessionID),N)  =>  (品类ID ( sessionID,N))
      val rdd4: RDD[(Long, (String, Int))] = rdd3.map {

        case ((categoryName, sessionId), count) => (categoryName, (sessionId, count))

      }

      // 分组，将品类ID相同的分组
      val rdd5: RDD[(Long, Iterable[(String, Int)])] = rdd4.groupByKey(1)

      //排序取前10
      val rdd6: RDD[(Long, List[(String, Int)])] = rdd5.mapValues(it => {

        //降序排序
        it.toList.sortBy(x => -x._2).take(10)

      })

      rdd6.saveAsTextFile(outputPath)


    }
  }
}
