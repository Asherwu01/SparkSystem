package com.asher.streaming.chapter10

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.junit._

class DStreamOutputTest {

  /*
      DStream保存为文件
   */
  @Test
  def test1(): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("dStream")

    val streamingContext = new StreamingContext(conf, Seconds(3))


    val ds1: ReceiverInputDStream[String] = streamingContext.socketTextStream("hadoop102", 9999)

    //先定义好窗口，之后的运算都在窗口中计算
    val result: DStream[(String, Int)] = ds1
      .window(Seconds(6))
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)

    // 前缀和后缀是输出目录的前缀和后缀       前缀+周期的时间戳+后缀
    //  一般将结果保存到数据库
    result.repartition(1).saveAsTextFiles("a","out")

    // foreachPartition 一个分区调用函数处理一次
    result.foreachRDD(rdd => rdd.foreachPartition(it => {
      // 新建数据库连接
      // 准备sql
      // 写出数据

    }))

    //启动运算
    streamingContext.start()
    //阻塞当前线程，直到终止
    streamingContext.awaitTermination()
  }

}
