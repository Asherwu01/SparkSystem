package com.asher.spark.tutorial.streaming_project.app


import com.asher.spark.tutorial.streaming_project.bean.AdsInfo
import com.asher.spark.tutorial.streaming_project.util.PropertiesUtil
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by VULCAN on 2020/7/24
 */
abstract class BaseApp extends  Serializable {

  // 提供一个context
  val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("streamingproject")

  val context = new StreamingContext(conf, Seconds(5))

  def runApp(op: => Unit)={

    op

    context.start()

    context.awaitTermination()

  }

  //kafka消费者的参数
  val kafkaParams = Map[String,String](
    "group.id" -> PropertiesUtil.getValue("kafka.group.id"),
    "bootstrap.servers" -> PropertiesUtil.getValue("kafka.broker.list"),
    "client.id" -> "1",
    "auto.offset.reset" -> "earliest",
    "auto.commit.interval.ms"->"500",
    "enable.auto.commit"->"true",
    "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
  )


  //提供方法，直接从主题供将数据进行消费，消费后返回Dstream
  def getDataFromKafka():InputDStream[ConsumerRecord[String, String]]={
    val ds: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](context,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](List(PropertiesUtil.getValue("kafka.topic")), kafkaParams))
    ds
  }

  //将Dstream中的consumerRecored中的数据，封装为AdsInfo
  def getAllBeans(ds:InputDStream[ConsumerRecord[String, String]])={

    //1595574107122,华北,北京,103,1   record.value()
    val result: DStream[AdsInfo] = ds.map(record => {
      val words: Array[String] = record.value().split(",")
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
