package com.asher.spark.tutorial.core_project.app

import com.asher.spark.tutorial.core_project.base.BaseApp
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

/**
 * Created by VULCAN on 2020/7/18
 */
object Top10jHotCategoryApp2 extends  BaseApp{

  override val outPutPath: String = "output/Top10jHotCategoryApp4"

  def main(args: Array[String]): Unit = {

    // 一次性封装所有的数据 ： 封装时，格式：   (类别 , clickcount, orderCount, payCountg)    xxx union all
    val sourceRDD: RDD[String] = sc.textFile("datas/user_visit_action.txt")

    // 封装好的数据
    val dataToMerge: RDD[(String, (Int, Int, Int))] = sourceRDD.flatMap(line => {

      val words: Array[String] = line.split("_")

      //判断是否是点击数据
      if (words(6) != "-1") {

        // 将单个元组包装为集合，   为了if条件判断中的格式统一
        List((words(6), (1, 0, 0)))

      } else if (words(8) != "null") {

        val categorys: Array[String] = words(8).split(",")

        categorys.map(category => (category, (0, 1, 0)))

      } else if (words(10) != "null") {

        val categorys: Array[String] = words(10).split(",")

        categorys.map(category => (category, (0, 0, 1)))

      } else {
        //返回空集合
        Nil
      }

    })

    //  dataToMerge 按照类别聚合     groupby
    val mergeResult: RDD[(String, (Int, Int, Int))] = dataToMerge.reduceByKey {

      case ((clickCount1, orderCount1, payCount1), (clickCount2, orderCount2, payCount2)) => (clickCount1 + clickCount2,
        orderCount1 + orderCount2, payCount1 + payCount2)

    }

    val finalResult: Array[(String, (Int, Int, Int))] = mergeResult.sortBy(x => x._2, numPartitions = 1)(
      Ordering.Tuple3(Ordering.Int.reverse, Ordering.Int.reverse, Ordering.Int.reverse), ClassTag(classOf[Tuple3[Int, Int, Int]])).take(10)

    // 收集到本地
    /*val finalResult: List[(String, (Int, Int, Int))] = mergeResult.collect().toList.sortBy(x => x._2)(
      Ordering.Tuple3(Ordering.Int.reverse, Ordering.Int.reverse, Ordering.Int.reverse)).take(10)*/

    //写出
   /* sc.makeRDD(finalResult,1).saveAsTextFile(outPutPath)*/

    sc.makeRDD(finalResult,1).saveAsTextFile(outPutPath)

  }
}
