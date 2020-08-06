package com.asher.spark.sql_project.udaf

import java.text.DecimalFormat

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

/*
需求：
   这里的热门商品是从点击量的维度来看的，计算各个区域前三大热门商品，
   并备注上每个商品在主要城市中的分布比例，超过两个城市用其他显示
   city_info:
        1	北京	华北
        2	上海	华东
        3	深圳	华南
   user_visit_action:
        2019-07-17	95	26070e87-1ad7-49a3-8fb3-cc741facaddf	37	2019-07-17 00:00:02	手机	-1	-1	\N	\N	\N	\N	3
   product_info:
        1	商品_1	自营
        2	商品_2	自营
        3	商品_3	自营

目标显示结果为
    地区  商品名称  点击次数     城市备注
    华北	  商品A	  100000    北京21.2%，天津13.2%，其他65.6%
    华北	  商品P	  80200	    北京63.0%，太原10%，其他27.0%
    华北	  商品M	  40000	    北京63.0%，太原10%，其他27.0%
    东北	  商品J	  92000	    大连28%，辽宁17.0%，其他 55.0%

分析：
  1.先计算各个区域前三大热门商品
     以区域和商品id分组，得出每个区域每个商品的点击次数
     再按点击次数求出前三

    所需字段：area  city_name  product_name
    思路：
      （1）三表关联，取出所需字段
          city_info           city_id(关联字段)  city_name   area
          product_info        product_id(关联字段)  product_name
          user_visit_action   city_id click_product_id(根据此字段计算次数)

      （2）计算每个商品在每个区域的所有城市的总点击次数(此时计算区域的总次数)
            以区域和商品分组，计算总次数
            相同城市的累加在UDAF中进行（此步骤在下面udaf中进行）
      （3）以区域为范围，对每个商品的点击次数进行排序，并取前三的次数

     实现：
        (select ci.city_id,ci.area,ci.city_name,pi.product_name
        from user_visit_action uva join city_id ci on uva.city_id=ci.city_id
        join product_info pi on uva.click_product_id=pi.product_id) t1

        (select area,product_name,count(1) clickCount
        from t1
        group by area,product_id,product_name) t2

        (select area,product_name,clickCount,rank() over(partition by area order by clickCount desc) rn
        from t2)  t3

        select area,product_name,clickCount
        from t3
        where rn <3





  2.自定义UDAF，求出 每个区域每个商品 在当前城市的点击次数和区域中所有城市的总的点击次数
    MyUDAF 用于分组后计算同一区域，某个商品在不同城市的点击次数，故输入列为 city_name

    select myUDAF(city_name)
    from xxx
    group by area,product_id

    input:   city_name string
    buffer:
              Map[String,Long]城市-此商品点击总次数（点击次数累加）
              Long：此商品在此区域的点击总次数
    output:  string :  北京21.2%，天津13.2%，其他65.6%

    需要一个 sum 区域总点击次数
          count 每个城市的点击次数
    基于1中（3），每个商品的总点击次数有不同城市组成，分别计算出当前区域，每个城市的点击次数占比，取前2，剩下用其它表示

 */

class MyUDAF extends UserDefinedAggregateFunction {
  //输入数据的结构
  override def inputSchema: StructType = StructType(StructField("city", StringType) :: Nil)

  //缓冲数据的结构
  override def bufferSchema: StructType = StructType(StructField("map", MapType(StringType, LongType)) ::
    StructField("sum", LongType) :: Nil)

  //返回数据的结构
  override def dataType: DataType = StringType

  //是否是确定性函数
  override def deterministic: Boolean = true

  //初始化缓冲区
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = Map[String, Long]()
    buffer(1) = 0L
  }

  //分区内计算，将input的值累加到buffer
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    //返回不可变map，input为城市名
    val map = buffer.getMap[String, Long](0)
    //累加城市名
    val key = input.getString(0)
    val value = map.getOrElse(key, 0L) + 1
    buffer(0) = map.updated(key, value)

    //当前区域总数 +1
    buffer(1) = buffer.getLong(1) + 1

  }

  //分区间计算，将buffer2的值累加到buffer1上
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    //map 合并
    val map1 = buffer1.getMap[String, Long](0)
    val map2 = buffer2.getMap[String, Long](0)
    val map3 = map2.foldLeft(map1) {
      case (map, (city, count)) => {
        val value = map.getOrElse(city, 0L) + count
        map.updated(city, value)
      }
    }
    buffer1(0)=map3


    //sum 合并
    val sum1 = buffer1.getLong(1)
    val sum2 = buffer2.getLong(1)
    buffer1(1) = sum1 + sum2
  }

  private val format = new DecimalFormat("0.00%")
  //返回最终结果
  override def evaluate(buffer: Row): Any = {
    //北京21.2%，天津13.2%，其他65.6%
    val map = buffer.getMap[String, Long](0)
    val resList = map.toList.sortBy(-_._2)

    //前二城市
    var top2 = resList.take(2)

    //剩余城市
    val sum = buffer.getLong(1)
    val otherCount = sum - top2(0)._2-top2(1)._2
    val result = top2 :+ ("其它",otherCount)

    //拼接字符串，并返回
    ""+result(0)._1 + format.format(result(0)._2.toDouble/sum)+", "+
      result(1)._1 + format.format(result(1)._2.toDouble/sum)+", "+
      result(2)._1 + format.format(result(2)._2.toDouble/sum)

  }
}
