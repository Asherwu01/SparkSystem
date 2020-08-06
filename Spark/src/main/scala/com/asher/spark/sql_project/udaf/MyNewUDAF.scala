package com.asher.spark.sql_project.udaf

import java.text.DecimalFormat

import org.apache.spark.sql.{Encoder, Encoders}
import org.apache.spark.sql.expressions.Aggregator

class MyNewUDAF extends Aggregator[String,MyBuffer,String]{
  //初始化MyBuffer
  override def zero: MyBuffer = MyBuffer(Map[String,Long](),0L)

  //分区内合并
  override def reduce(b: MyBuffer, a: String): MyBuffer = {
    //返回不可变map，input为城市名
    val map = b.map
    //累加城市名
    val key = a
    val value = map.getOrElse(key, 0L) + 1
    b.map = map.updated(key, value)

    //当前区域总数 +1
    b.sum = b.sum + 1

    b
  }

  //分区间的合并，b2合并到b1
  override def merge(b1: MyBuffer, b2: MyBuffer): MyBuffer = {
    //map 合并
    val map1 = b1.map
    val map2 = b2.map
    val map3 = map2.foldLeft(map1) {
      case (map, (city, count)) => {
        val value = map.getOrElse(city, 0L) + count
        map.updated(city, value)
      }
    }
    b1.map=map3


    //sum 合并
    val sum1 = b1.sum
    val sum2 = b2.sum
    b1.sum = sum1 + sum2

    b1
  }

  //返回最终结果
  private val format = new DecimalFormat("0.00%")
  override def finish(reduction: MyBuffer): String = {
    //北京21.2%，天津13.2%，其他65.6%
    val map = reduction.map
    val reList = map.toList.sortBy(-_._2)

    //前二城市
    var top2: List[(String, Long)] = reList.take(2)

    //剩余城市
    val sum = reduction.sum
    val otherCount = sum - top2(0)._2-top2(1)._2
    val result = top2 :+ ("其它",otherCount)

    //拼接字符串，并返回
    ""+result(0)._1 + format.format(result(0)._2.toDouble/sum)+", "+
      result(1)._1 + format.format(result(1)._2.toDouble/sum)+", "+
      result(2)._1 + format.format(result(2)._2.toDouble/sum)//北京21.2%，天津13.2%，其他65.6%

  }

  //buffer的编码器
  override def bufferEncoder: Encoder[MyBuffer] = Encoders.product

  //输出的编码器
  override def outputEncoder: Encoder[String] = Encoders.STRING
}

case class MyBuffer(var map:Map[String,Long],var sum:Long)