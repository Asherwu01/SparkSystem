package com.asher.spark.stream_project.app

import com.asher.spark.stream_project.bean.AdsInfo
import com.asher.spark.stream_project.util.PropertiesUtil
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/*
    使用有状态转换，需要设置检查点，默认保存在文件系统中，
    也可以手动设置检查点，将文件保存在数据库或者mysql中。
 */

abstract class BaseApp extends Serializable {
  //提供streamContext
  val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("stream_project")
  val streamingContext = new StreamingContext(conf, Seconds(5))

  //要运行的代码
  def runApp(opt: => Unit) = {
    opt
    streamingContext.start()

    streamingContext.awaitTermination()
  }

  //kafka消费者的参数
  val kafkaParams: Map[String, String] = Map[String, String](
    "group.id" -> PropertiesUtil.getValue("kafka.group.id"),
    "bootstrap.servers" -> PropertiesUtil.getValue("kafka.broker.list"),
    "client.id" -> "1",
    "auto.offset.reset" -> "earliest",
    "auto.commit.interval.ms" -> "500",
    "enable.auto.commit" -> "true",
    "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
  )

  //将kafka作为数据源，创建dstream，消费kafka中的数据
  //1595587573041,华北,北京,104,5
  def getDataFromKafka() = {

    //采用直连方式，减少Executor之间数据传输；采用订阅模式，让系统来维护partition和offset
    val dStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](List(PropertiesUtil.getValue("kafka.topic")), kafkaParams)
    )
    dStream
  }


  //将dStream中每一批数据，封装成bean,返回新的dStream，
  //1595587573041,华北,北京,104,5,kafka中的某条消息
  def getAllBeans(dS: InputDStream[ConsumerRecord[String, String]]): DStream[AdsInfo] = {
    val result = dS.map(record => {
      val value = record.value() //这是一行的内容
      val words = value.split(",")
      AdsInfo(
        words(0).toLong,
        words(1),
        words(2),
        words(3),
        words(4)
      )

    })
    result
  }


}
