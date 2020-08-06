package com.asher.spark.tutorial.sql_project.udaf

import java.text.DecimalFormat

import org.apache.spark.sql.{Encoder, Encoders}
import org.apache.spark.sql.expressions.Aggregator

/**
 * Created by VULCAN on 2020/7/23
 *
 * 输入：  string : cityname
 * *         buffer:  Map[String,Long] 保存每个城市的点击次数
 * *                  Long ： 计算此商品在此地区的点击总数
 * *
 * *         输出：  string :  北京21.2%，天津13.2%，其他65.6%
 * *
 * *         21.2% = 城市的点击次数 / 地区的总点击次数
 *
 * IN: 输入的类型     string : cityname
 * BUFFER： 缓冲区类型
 *                      Map[String,Long] 保存每个城市的点击次数
 * * *                  Long ： 计算此商品在此地区的点击总数
 * OUT ： 输出类型  string :  北京21.2%，天津13.2%，其他65.6%
 */
class MyNewUDAF extends Aggregator[String,MyBuffer,String]{

  // 初始化缓冲区
  override def zero: MyBuffer = MyBuffer(Map[String,Long](),0l)

  // 分区内聚合 将a聚合到b上返回b
  override def reduce(b: MyBuffer, a: String): MyBuffer = {

    val map: Map[String, Long] = b.map

    val key: String = a

    val value: Long = map.getOrElse(key, 0l)+1l

    b.map=map.updated(key,value)

    // 为总数量+1
    b.sum =b.sum + 1

    b

  }

  //分区间聚合，将b2的值聚合到b1上返回b1
  override def merge(b1: MyBuffer, b2: MyBuffer): MyBuffer = {

    val map2: Map[String, Long] = b2.map
    val map1: Map[String, Long] = b1.map

    // foldLeft(zeroValue)(f:(zeroValue,elem))  从左向右
    val result: Map[String, Long] = map2.foldLeft(map1) {

      case (map, (city, count)) => {

        val sumCount: Long = map.getOrElse(city, 0L) + count
        map.updated(city, sumCount)

      }
    }

    b1.map=result
    //合并sum
    b1.sum= b2.sum+b1.sum

    b1

  }

  private val format = new DecimalFormat("0.00%")

  // 返回结果
  override def finish(reduction: MyBuffer): String = {

    //对resultMap进行降序排序，取前2
    val top2: List[(String, Long)] = reduction.map.toList.sortBy(-_._2).take(2)

    //计算其他的数量
    val otherCount: Long = reduction.sum - top2(0)._2 - top2(1)._2

    //加入其他
    val result: List[(String, Long)] = top2 :+ ("其他", otherCount)

    //拼接字符串
    val str: String = ""+result(0)._1+" "+format.format(result(0)._2/reduction.sum.toDouble) +
      result(1)._1+" "+format.format(result(1)._2/reduction.sum.toDouble)+
      result(2)._1+" "+format.format(result(2)._2/reduction.sum.toDouble)

    str

  }

  // buffer的编码器
  override def bufferEncoder: Encoder[MyBuffer] = Encoders.product

  // 输出的编码器
  override def outputEncoder: Encoder[String] = Encoders.STRING
}

case class MyBuffer(var map:Map[String,Long],var sum:Long)
