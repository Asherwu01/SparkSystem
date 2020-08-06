package com.asher.rdd.chapter5

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.reflect.ClassTag

/**
 *    时间戳，省份，城市，用户，广告，中间字段使用空格分隔
 *        数据格式： 1516609143867 6 7 64 16
 *
 *        统计出每一个省份 广告点击数量排行的Top3 的广告
 *
 *        rdd  --map-->( ((省份,广告) ,1), (省份,广告) ,1), (省份,广告) ,1)   )--->reduce--->
 *        ( ((省份1,广告1) ,6), ((省份1,广告2) ,4), (省份2,广告1) ,3)   )---map--->
 *        ( (省份1,(广告1 ,6)), (省份1,(广告2 ,4)), (省份2,(广告1 ,3))   )-->group-->
 *        ( (省份1，((广告1,6),(广告2，4))) )-->sortby-->( (省份1，((广告1,6),(广告2，4))) )
 */
object Exercise {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setMaster("local").setAppName("My App"))

    val rdd1 = sc.textFile("Spark/input/agent.log")

    val rdd2 = rdd1.map(line => {
      val arr = line.split(" ")
      (arr(1), arr(4)) -> 1
    })

    val rdd3: RDD[((String, String), Int)] = rdd2.reduceByKey(_ + _)

    val rdd4 = rdd3.map {
      case ((province, ads), count) => (province, (ads, count))
    }

    val rdd5: RDD[(String, Iterable[(String, Int)])] = rdd4.groupByKey(1)

    val rdd6: RDD[(String, List[(String, Int)])] = rdd5.map {
      case (province, iter) => province -> iter.toList
    }

    /*val rdd7 = rdd6.mapValues(value => {
      value.sortBy(kv => kv._2)(Ordering[Int].reverse).take(3)
    })*/
    val rdd7 = rdd6.map {
      case (province, list) => (province, list.sortBy(_._2)(Ordering[Int].reverse).take(3))
    }
    rdd7.saveAsTextFile("Spark/output1")
  }
}
