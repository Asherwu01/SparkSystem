package com.asher.streaming.chapter9

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.junit._

import scala.collection.mutable

/**
 *    离线(不在线)数据：   不是此时此刻正在生成的数据！明确的生命周期！
 *    实时数据：   此时此刻正在生成的数据！只有开始没有结束！ 源源不断的流式数据！
 *
 *    离线计算：   计算需要花费一定的周期，才能生成结果！不能立刻，现在就出结果！
 *    实时计算：    立刻，现在就出结果！
 *
 *    计算引擎：mr、spark、flink
 *    计算引擎计算 都可以计算实时数据和离线数据，如果立刻出结果，则为实时计算，不能立刻出结果，则为离线计算。
 *
 *
 *    Spark Streaming： 计算方式是： 准实时计算！ 接近实时！  微批次处理！  本质上还是批处理！每次处理的一批数据量不是很大！
 *
 *              流式数据！ 数据是不是源源不断！
 *
 *     基本的数据抽象： 定义数据源，定义数据的处理逻辑
 *        SparkCore :  RDD
 *        SparkSql  :  DataFrame,DataSet
 *                        对RDD的封装！
 *        SparkStreaming:  DStream(离散化流)，流式数据可以离散分布到多个Excutor进行并行计算！
 *                                本质还是对RDD的封装
 *
 *
 *      数据的语义：
 *          at most once :  至多一次  0次或1次，会丢数据！
 *          at least once： 至少一次。 不会丢数据！  有可能会重复！
 *          exactly once： 精准一次
 *
 *         xxx 框架，支持 at least once
 *
 *
 *      整体架构：  ①SparkStreaming一开始运行，就需要有Driver，还必须申请一个Executor，运行一个不会停止的task!
 *                      这个task负责运行 reciever，不断接受数据
 *
 *                 ②满足一个批次数据后，向Driver汇报，生成Job，提交运行Job，由 reciever将这批数据的副本发送到 Job运行的Exceutor
 *
 *
 *
 *    有StreamingContext作为应用程序上下文：
 *            只能使用辅助构造器构造！
 *            可以基于masterurl和appname构建，或基于sparkconf构建，或基于一个SparkContext构建！
 *
 *            获取StreamingContext中关联的SparkContext： StreamingContext.SparkContext
 *
 *            得到或转换了Dstream后，Dstream的计算逻辑，会在StreamingContext.start()后执行，在StreamingContext.stop()后结束！
 * *
 *            StreamingContext.awaitTermination()： 阻塞当前线程，直到出现了异常或StreamingContext.stop()！
 *
 *            作用： 创建Dstream(最基本的数据抽象模型)
 */
class WorldCountTest {

  /*
        使用netcat 绑定一个指定的端口，让SparkStreaming程序，读取端口指定的输出信息！

        基于SparkConf来获取SparkStream
   */
  @Test
  def test1(): Unit = {
    //创建StreamContext
    val conf = new SparkConf().setMaster("local[*]").setAppName("DStream")
    val ssc = new StreamingContext(conf, Seconds(5))

    //获取数据抽象DStream，socketTextStream()默认以 \n 作为一条数据，每行数据一条
    val dStream1 = ssc.socketTextStream("hadoop102", 9999)

    //执行各种原语，统计单词个数
    val dStream2 = dStream1.flatMap(_.split(" ")).map(_ -> 1).reduceByKey(_ + _)

    //输入每个窗口中的结果RDD的前100条
    dStream2.print(100)

    //启动当前线程
    ssc.start()

    //阻塞当前线程，直至调用stop结束线程或出现异常
    ssc.awaitTermination()

  }

  /*
    创建一个Quene[RDD],让接收器每次接受队列中的一个RDD进行运算！
      def queueStream[T: ClassTag](
        queue: Queue[RDD[T]],    读取数据的队列
        oneAtATime: Boolean,   每个周期是否仅从 队列中读取一个RDD
        defaultRDD: RDD[T]    如果队列中的RDD被消费完了，返回一个默认的RDD，通常为null
      ): InputDStream[T] = {
        new QueueInputDStream(this, queue, oneAtATime, defaultRDD)
      }
   */
  @Test
  def test2(): Unit = {
    //创建StreamContext
    val conf = new SparkConf().setMaster("local[*]").setAppName("DStream")
    val ssc = new StreamingContext(conf, Seconds(2))

    val queue = mutable.Queue[RDD[String]]()

    val dStream = ssc.queueStream(queue, false, null)
    //进行wordcount
    val result = dStream.flatMap(_.split(" ")).map(_ -> 1).reduceByKey(_ + _)

    result.print(100)

    //启动当前线程
    ssc.start()

    val rdd = ssc.sparkContext.makeRDD(List("hello", "world", "hello"))
    //向queue中加rdd
    for(i <- 1 to 100){
      Thread.sleep(1000)
      queue.enqueue(rdd)
    }

    //阻塞当前线程，直至调用stop结束线程或出现异常
    ssc.awaitTermination()
  }
}
