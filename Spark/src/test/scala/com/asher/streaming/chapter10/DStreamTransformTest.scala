package com.asher.streaming.chapter10

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.junit._

/**
 * Dsstream的转换分为无状态和有状态！
 *
 * 状态(state) : 是否可以保留之前批次处理的结果！
 * 无状态：  不会保留之前批次处理的结果！ 每个批次都是独立的，是割裂的！
 * 有状态：  会保留之前批次处理的结果，新的批次在处理时，可以基于之前批次的结果，进行组合运算！
 */
class DStreamTransformTest {

  /*
      无状态转换；
      transform :  将一个Dstream，利用函数，将这个Dstream中的所有的RDD，转换为其他的RDD，之后再返回新的Dstream
      将Dstream的元素转换为RDD，调用RDD的算子进行计算！
      DStream只给你存放元素的类型取决与数据源的类型
   */
  @Test
  def test1(): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("dstream")
    val ssc = new StreamingContext(conf, Seconds(3))

    //从端口监听数据源获取DStream
    val ds1 = ssc.socketTextStream("hadoop102", 9999)
    val ds2 = ds1.transform(rdd => {
      val rdd2 = rdd.flatMap(_.split(" ")).map(_ -> 1)
      rdd2
    })

    //输出每个窗口计算后的rdd的结果的前100条
    ds2.print(100)

    //启动运算
    ssc.start()

    //阻塞当前线程
    ssc.awaitTermination()
  }

  /*
      无状态转换：transform
       不用Dstream的转换原语，而是用状态无状态转换函数transform， 将dStream中每个元素(dS中元素是以rdd来存放的)转为DS或DF，在进行对应操作
            Dstream  =>  RDD  =>  DS /DF
   */
  @Test
  def test2(): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("dstream")

    val ssc = new StreamingContext(conf, Seconds(3))

    //获取sparkContext
    //val sc = new SparkContext(conf)
    val sc = ssc.sparkContext

    //基于conf构建sparkSession
    val spark = SparkSession.builder().config(conf).getOrCreate()
    import spark.implicits._

    //获取dStream
    val dStream = ssc.socketTextStream("hadoop102", 9999)
    //用df或ds处理dStream中的数据
    val dStream2 = dStream.transform(rdd => {
      val df = rdd.toDF()
      df.createOrReplaceTempView("tmp")
      val df1 = spark.sql("select * from tmp")
      df1.show()
      df1.rdd
    })

    val result = dStream2.map(e => e.getString(0))
    result.print(100)

    ssc.start()

    ssc.awaitTermination()
  }

  /*
      无状态转换：Join
      Join:  双流Join
               两种实时计算逻辑需求的场景！
             流越多，需要的计算资源就越多！
             采集周期必须是一致的！
   */
  @Test
  def test3(): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("dStream")

    // 一个应用里面只能有一个StreamingContext，一个应用中，采集周期是统一的！
    val streamContext = new StreamingContext(conf, Seconds(5))
    //val streamContext2 = new StreamingContext(conf, Seconds(3))

    //获取数据抽象 Dstream  默认以\n作为一条数据，每行数据为一条
    val ds1: ReceiverInputDStream[String] = streamContext.socketTextStream("hadoop102", 9999)
    val ds2: ReceiverInputDStream[String] = streamContext.socketTextStream("hadoop102", 3333)

    // 流中的内容必须是k-v类型
    val ds3: DStream[(String, Int)] = ds1.flatMap(line => line.split(" ")).map((_, 1)).reduceByKey(_ + _)
    val ds4: DStream[(String, Int)] = ds2.flatMap(line => line.split(" ")).map((_, 1)).reduceByKey(_ + _)

    val ds5: DStream[(String, (Int, Int))] = ds3.join(ds4)

    //输出每个窗口计算后的结果的RDD的前100条数据！
    ds5.print(100)

    //启动运算
    streamContext.start()
    //sparkContext2.start()

    //阻塞当前线程，知道终止
    streamContext.awaitTermination()
    // sparkContext2.awaitTermination()
  }

  /*
      有状态的计算:updateStateByKey(此方法需要checkpoint)

      有状态的计算，由于需要checkpoint保存之前周期计算的状态，会造成小文件过多！
          解决： ①自己通过程序将过期的小文件删除
                ②不使用checkpoint机制，而是自己持久化状态到数据库中
       统计从此刻开始，数据的累计状态！  将多个采集周期计算的结果进行累加运算！
            UpdateStateByKey

    def updateStateByKey[S: ClassTag](       // 数据是k-v类型
      updateFunc: (Seq[V], Option[S]) => Option[S] ): DStream[(K, S)]
      updateFunc 会被当前采集周期中的每个key都进行调用，调用后将
                                        当前key的若干value和之前采集周期中key对应的最终的State进行合并，合并后更新最新状态
                                        Seq[V]： 当前采集周期中key对应的values
                                        Option[S]: 之前采集周期,key最新的State
   */
  @Test
  def test4(): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("dStream")

    // 一个应用里面只能有一个StreamingContext，一个应用中，采集周期是统一的！
    val streamContext = new StreamingContext(conf, Seconds(5))

    //要保存之前的状态，
    streamContext.checkpoint("updateStateByKey")

    val ds1: ReceiverInputDStream[String] = streamContext.socketTextStream("hadoop102", 9999)

    val ds2 = ds1
      .flatMap(line => line.split(" "))
      .map((_, 1))
      .updateStateByKey((seq: Seq[Int], opt: Option[Int]) => Some(seq.sum + opt.getOrElse(0)))

    ds2.print(100)

    //启动运算
    streamContext.start()

    //阻塞当前线程，知道终止
    streamContext.awaitTermination()
  }


  /*
      有状态计算：reduceByKeyAndWindow
      此方法不需要checkpoint，原因：当开了窗口后，窗口向前滑动时，结果是已经保存在窗口中了，也就是窗口之间没有结果关联
                                注意，此方法还有重载方法，如果有参数 invReduceFunc,由于重复的部分数据已经计算过，不需要重写计算，直接保存，此时不同窗口结果又关系，需要checkpoint
      updateStateByKey需要checkpoint，是因为它的默认窗口大小和步长就是采集周期，而且窗口之间结果有关联，故需要checkpoint
      无状态转换，相当于默认窗口大小和步长就是采集周期，窗口之间没有结果关联，故不需要checkpoint

      需求：每间隔3s,统计3s内，所有的数据！

         def reduceByKeyAndWindow(
            reduceFunc: (V, V) => V,      计算的函数
            windowDuration: Duration,     窗口范围（批次周期的多少倍）
            slideDuration: Duration        滑动的步长（等于批次周期的多少倍）
          ): DStream[(K, V)]

    reduceByKeyAndWindow: 在一个滑动的窗口内，计算一次！

        滑动步长和窗口的范围必须是采集周期的整倍数！
    reduceByKey：  一个采集周期（批次）计算一次！

    采集周期：  可以看作，reduceByKeyAndWindow中计算的窗口大小和采集周期一致，且滑动的步长和采集周期一致的特殊情况。
   */
  @Test
  def test5(): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("dStream")
    val streamContext = new StreamingContext(conf, Seconds(1))


    val ds1: ReceiverInputDStream[String] = streamContext.socketTextStream("hadoop102", 9999)

    val ds2 = ds1
      .flatMap(line => line.split(" "))
      .map((_, 1))
      .reduceByKeyAndWindow(_+_,windowDuration=Seconds(3),slideDuration = Seconds(3))

    ds2.print(100)

    //启动运算
    streamContext.start()

    //阻塞当前线程，知道终止
    streamContext.awaitTermination()
  }

  /*
      如果窗口有重复计算，效率低，可以优化（注意，这里优化，依然还是要统计重复数据，因为窗口大小和步长是一定的）
      def reduceByKeyAndWindow(
            reduceFunc: (V, V) => V,
            invReduceFunc: (V, V) => V,
            windowDuration: Duration,
            slideDuration: Duration = self.slideDuration,
            numPartitions: Int = ssc.sc.defaultParallelism,
            filterFunc: ((K, V)) => Boolean = null
          ): DStream[(K, V)]
      如果有参数 invReduceFunc,由于重复的部分数据已经计算过，不需要重写计算，直接保存，此时不同窗口结果又关系，需要checkpoint
   */
  @Test
  def test6(): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("dStream")
    val streamContext = new StreamingContext(conf, Seconds(3))

    //要保存之前的状态，
    streamContext.checkpoint("ck")

    val ds1: ReceiverInputDStream[String] = streamContext.socketTextStream("hadoop102", 9999)

    val ds2 = ds1
      .flatMap(line => line.split(" "))
      .map((_, 1))
      .reduceByKeyAndWindow(_+_,_+_,windowDuration=Seconds(6),slideDuration = Seconds(3))

    ds2.print(100)

    //启动运算
    streamContext.start()

    //阻塞当前线程，知道终止
    streamContext.awaitTermination()
  }

  /*
        先定义好窗口，在窗口中计算
   */
  @Test
  def test7(): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("dStream")
    val streamContext = new StreamingContext(conf, Seconds(3))

    val ds1: ReceiverInputDStream[String] = streamContext.socketTextStream("hadoop102", 9999)

    val ds2 = ds1.window(Seconds(6), Seconds(3))
      .flatMap(_.split(" "))
      .map(_ -> 1)
      .reduceByKey(_ + _)

    ds2.print(100)

    streamContext.start()
    streamContext.awaitTermination()
  }


  /*
      优雅地关闭流式程序
       streamContext.stop()
   */
  @Test
  def test8(): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("dStream")
    val streamContext = new StreamingContext(conf, Seconds(3))

    val ds1: ReceiverInputDStream[String] = streamContext.socketTextStream("hadoop102", 9999)

    val ds2 = ds1.window(Seconds(6), Seconds(3))
      .flatMap(_.split(" "))
      .map(_ -> 1)
      .reduceByKey(_ + _)

    ds2.print(100)

    streamContext.start()
    streamContext.awaitTermination()//下面代码依然会执行，此方法知识组织当前线程结束，并不阻止下面代码的执行
    //在新线程中关闭
    new Thread(){
      setDaemon(true)
      override def run(): Unit = {
        //在需要关闭的时候才关闭
        // true = 程序代码，用代码去判断当前是否需要关闭
        //  程序可以尝试去读取一个数据库，或一个指定路径的标识符   /close(true)
        var status = true  //true为不关闭，false为关闭，通常从外部读取
        while(!true){
          Thread.sleep(5000)
        }

        streamContext.stop(true,true)

      }
    }.start()
  }

  /*
        一下两种情况的区别：
        Dstream => transform => RDD/DF/DS
                                RDD.map
        Dstream.map

   */
  @Test
  def test9(): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("df")

    val streamingContext = new StreamingContext(conf, Seconds(3))

    val ds1: ReceiverInputDStream[String] = streamingContext.socketTextStream("hadoop102", 9999)

    //直接ds.map，直接调用 map,相当于直接调用rdd的map，在Executor端
    val ds11: DStream[String] = ds1.map(x => {
      println(Thread.currentThread().getName + ": ds1.map")
      x
    })

    //ds -->transform--> rdd.map  在Driver端，调用transform原语，在driver，调用map，其实调用的rdd的map，在executor端
    val d12: DStream[String] = ds1.transform(rdd => {
      //JobGenerator  Driver
      println(Thread.currentThread().getName + ": ds1.transform")
      val rdd2 = rdd.map(x => {
        //exc
        println(Thread.currentThread().getName + ": rdd.map")
        x
      })
      rdd2
    })

    //ds11.print(100)
    d12.print(100)

    streamingContext.start()
    streamingContext.awaitTermination()
  }

}
