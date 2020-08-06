package com.asher.streaming.chapter9

/**
 * 需求：通过SparkStreaming从Kafka读取数据，并将读取过来的数据做简单计算(单词统计)，最终打印到控制台。
 */

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.junit._

class KafKaDirectMode {

  /*
     def createDirectStream[K, V](
       ssc: StreamingContext,      上下文
       locationStrategy: LocationStrategy,   为Executor中的消费者线程分配主题分区的负责均衡策略，通常都用这个！
       consumerStrategy: ConsumerStrategy[K, V] ： ConsumerStrategies.Subscribe  系统自动为消费者组分配分区！自动维护offset!
       ): InputDStream[ConsumerRecord[K, V]] = {
        val ppc = new DefaultPerPartitionConfig(ssc.sparkContext.getConf)
        createDirectStream[K, V](ssc, locationStrategy, consumerStrategy, ppc)
     }
   */
  @Test
  def test1(): Unit = {
    //创建StreamContext
    val conf = new SparkConf().setMaster("local[*]").setAppName("DStream")
    val ssc = new StreamingContext(conf, Seconds(2))

    /*
         ConsumerConfig
         earliest： 只在消费者组不存在时生效！
    */
    val kafkaParams = Map[String, String](
      "group.id" -> "asher",
      "bootstrap.servers" -> "hadoop102:9092",
      "client.id" -> "1",
      "auto.offset.reset" -> "earliest",
      "auto.commit.interval.ms" -> "500",
      "enable.auto.commit" -> "true",
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
    )

    //获取Reciever  Subscribe模式系统自动维护offset
    //消费kafka中数据
    val dS1 = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](List("hello"), kafkaParams)
    )

    //ConsumerRecord[String, String]将每一行数据都封装成k-v结构，获取value， 只处理value
    val dS2 = dS1.map(record => {
      /*if (record.value() == "d" ){
            throw new Exception("异常了")  //当抛出异常后，有与业务逻辑还未执行，而kafka中数据已经消费完，造成数据丢失；
            //解决：设置检查点，改为手动提交，业务逻辑执行完后再提交，可以通过去重，实现at least once ---> exactly once
      }*/
      record.value()
    })

    //业务逻辑，计算WordCount
    val dS3 = dS2.flatMap(_.split(" ")).map(_ -> 1).reduceByKey(_ + _)
    dS3.print(100)

    //启动当前线程
    ssc.start()

    //阻塞当前线程，直至调用stop结束线程或出现异常
    ssc.awaitTermination()
  }

  /*
        解决丢数据：
            ①取消自动提交
            ②在业务逻辑处理完成之后，再手动提交offset
              spark允许设置checkpoint，在故障时，自动将故障的之前的状态(包含为提交的offset)存储！
              可以在重启程序后，重建状态，继续处理！
   */
  @Test
  def test2(): Unit = {

    def rebuild(): StreamingContext = {
      //创建StreamContext
      val conf = new SparkConf().setMaster("local[*]").setAppName("DStream")
      val ssc = new StreamingContext(conf, Seconds(2))

      ssc.checkpoint("kafka")
      /*
           ConsumerConfig
           earliest： 只在消费者组不存在时生效！
      */
      val kafkaParams = Map[String, String](
        "group.id" -> "asher",
        "bootstrap.servers" -> "hadoop102:9092",
        "client.id" -> "1",
        "auto.offset.reset" -> "earliest",
        "auto.commit.interval.ms" -> "500",
        "enable.auto.commit" -> "false",
        "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
      )

      //获取Reciever  Subscribe模式系统自动维护offset
      //消费kafka中数据
      val dS1 = KafkaUtils.createDirectStream[String, String](
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](List("hello"), kafkaParams)
      )

      //ConsumerRecord[String, String]将每一行数据都封装成k-v结构，获取value， 只处理value
      val dS2 = dS1.map(record => {
        if (record.value() == "d") {
          //throw new Exception("异常了")  //当抛出异常后，有与业务逻辑还未执行，而kafka中数据已经消费完，造成数据丢失；
          //解决：设置检查点，改为手动提交，业务逻辑执行完后再提交，可以通过去重，实现at least once ---> exactly once
        }
        record.value()
      })

      //业务逻辑，计算WordCount
      val dS3 = dS2.flatMap(_.split(" ")).map(_ -> 1).reduceByKey(_ + _)
      dS3.print(100)

      ssc
    }


    /*
        获取一个active的streamContext，或从checkpoint的目录中重建streamContext，或new
     */
    val ssc = StreamingContext.getActiveOrCreate("kafka", rebuild)
    //启动当前线程
    ssc.start()

    //阻塞当前线程，直至调用stop结束线程或出现异常
    ssc.awaitTermination()
  }

  @Test
  def test0(): Unit = {
  }
}
