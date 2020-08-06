package com.asher.spark.core_project.boot

import com.asher.spark.core_project.base.BaseApp

import scala.reflect.ClassTag

object Top10HotCategoryApp2 extends BaseApp {
  override var outputPath: String = "Spark/project_output/Top10HotCategoryApp2"

  def main(args: Array[String]): Unit = {
    runApp {

      //原始数据
      val data = sc.textFile("Spark/core_project_input")

      // 一次性封装所有的数据 ： 封装时，格式：   (类别 , clickcount, orderCount, payCountg)    xxx union all
      // 统计点击数
      val mergeData = data.flatMap(line => {
        val words = line.split("_")
        //判断每行数据是否是点击数据
        if (words(6) != "-1") {
          List((words(6), (1, 0, 0)))
        } else if (words(8) != "null") {
          val arr = words(8).split(",")
          val tuples = arr.map(category => (category,(0, 1, 0)))
          tuples
        } else if (words(10) != "null") {
          val arr = words(10).split(",")
          val tuples = arr.map(category =>(category,(0, 1, 0)))
          tuples
        } else {
          Nil
        }
      })

      val mergeResult = mergeData.reduceByKey {
        case ((click1, order1, pay1), (click2, order2, pay2)) => (click1 + click2, order1 + order2, pay1 + pay2)
      }

      val finalResult: Array[(String, (Int, Int, Int))] = mergeResult.sortBy(x => x._2, numPartitions = 1)(
        Ordering.Tuple3(Ordering.Int.reverse, Ordering.Int.reverse, Ordering.Int.reverse), ClassTag(classOf[Tuple3[Int, Int, Int]])).take(10)

      sc.makeRDD(finalResult,1).saveAsTextFile(outputPath)
      /*
          转换成list，全部收集到内存，容易OOM
       */
      /*val finalResult = result.collect.toList.sortBy(_._2)(Ordering.Tuple3(Ordering.Int.reverse, Ordering.Int.reverse, Ordering.Int.reverse))
      sc.makeRDD(finalResult,1).saveAsTextFile(outputPath)*/
    }
  }
}
