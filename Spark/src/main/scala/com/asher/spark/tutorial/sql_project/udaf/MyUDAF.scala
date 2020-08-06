package com.asher.spark.tutorial.sql_project.udaf

import java.text.DecimalFormat

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

/**
 * Created by VULCAN on 2020/7/21
 *
 *   MyUDAF( cityname ) => 北京21.2%，天津13.2%，其他65.6%
 *
 *   作用：  统计每一个area,每一个product_id，不同的城市的点击次数
 *
 *              group by area,product_id
 *
 *        华北	商品A	 北京
 *        华北	商品A	 北京
 *        华北	商品A	 天津
 *        华北	商品A	 北京
 *
 *         MyUDAF( cityname ) 使用位置是，在   group by area,product_id 后进行统计
 *
 *         select  MyUDAF( cityname )
 *         from xx
 *         group by area,product_id
 *
 *         输入：  string : cityname
 *         buffer:  Map[String,Long] 保存每个城市的点击次数
 *                  Long ： 计算此商品在此地区的点击总数
 *
 *         输出：  string :  北京21.2%，天津13.2%，其他65.6%
 *
 *         21.2% = 城市的点击次数 / 地区的总点击次数
 *
 *
 *
 *
 *
 */
class MyUDAF extends  UserDefinedAggregateFunction{

  // 输入的参数的结构
  override def inputSchema: StructType = StructType(StructField("city",StringType)::Nil )

  // 缓冲区的结构
  override def bufferSchema: StructType = StructType(StructField("map",MapType(StringType,LongType))::
    StructField("sum",LongType)::Nil )

  //返回值的类型
  override def dataType: DataType = StringType

  //是否是确定性函数
  override def deterministic: Boolean = true

  // 初始化缓冲区
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0)=Map[String,Long]()
    buffer(1)=0l
  }

  //分区内计算  将input的值，累加到buffer
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    // input: Row  城市名
    // 为当前城市的点击数量+1  返回的是不可变的map
    val map: collection.Map[String, Long] = buffer.getMap[String, Long](0)

    val key: String = input.getString(0)
    val value: Long = map.getOrElse(key, 0l)+1l

    buffer(0)=map.updated(key,value)

    // 为总数量+1
    buffer(1) = buffer.getLong(1) + 1

  }

  //分区间计算  将buffer2的值，累加到buffer1
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {

    val map2: collection.Map[String, Long] = buffer2.getMap[String, Long](0)
    val map1: collection.Map[String, Long] = buffer1.getMap[String, Long](0)

    // foldLeft(zeroValue)(f:(zeroValue,elem))  从左向右
    val result: collection.Map[String, Long] = map2.foldLeft(map1) {

      case (map, (city, count)) => {

        val sumCount: Long = map.getOrElse(city, 0L) + count
        map.updated(city, sumCount)

      }
    }

    buffer1(0)=result
    //合并sum
    buffer1(1)= buffer2.getLong(1)+buffer1.getLong(1)
  }

  private val format = new DecimalFormat("0.00%")
  //返回最终结果
  override def evaluate(buffer: Row): Any = {

    //北京21.2%，天津13.2%，其他65.6%

    val resultMap: collection.Map[String, Long] = buffer.getMap[String, Long](0)

    val sum: Long = buffer.getLong(1)

    //对resultMap进行降序排序，取前2
    val top2: List[(String, Long)] = resultMap.toList.sortBy(-_._2).take(2)

    //计算其他的数量
    val otherCount: Long = sum - top2(0)._2 - top2(1)._2

    //加入其他
    val result: List[(String, Long)] = top2 :+ ("其他", otherCount)

    //拼接字符串
    val str: String = ""+result(0)._1+" "+format.format(result(0)._2/sum.toDouble) +
      result(1)._1+" "+format.format(result(1)._2/sum.toDouble)+
      result(2)._1+" "+format.format(result(2)._2/sum.toDouble)

    str

  }
}
