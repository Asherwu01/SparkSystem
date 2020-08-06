package com.asher.rdd.chapter5

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, RangePartitioner, SparkConf, SparkContext}
import org.junit._

/*
 * 1.行动算子和转换算子的区别
 *    转换算子： (transmation Operation)  只是将一个RDD另一个RDD。 是lazy
 *              只要算子的返回值是RDD，一定是转换算子！
 *    行动算子： (action Operation)  只有执行行动算子，才会触发整个Job的提交！
 *              只要算子的返回值不是RDD，一是行动算子！
 *
 * 2.reduce
 * 3.collect
 * 4.count
 * 5.first
 * 6.take
 * 7.takeOrdered
 * 8.aggregate
 * 9.fold
 * 10.countByKey
 * 11.countByValue
 * 12.save相关
 * 13.foreach
 * 14.特殊情况
 */
class ActionOperateTest {
  /*
      测试foreach在Driver和在不同Executor上的打印
   */
  @Test
  def test15(): Unit = {
    val list = List(1, 2, 3, 4)
    val rdd1 = sc.makeRDD(list, 2)

    //在Driver收集后打印
    rdd1.collect().foreach(x => println(x + "---" + Thread.currentThread().getName))
    println("=====================")
    //在Executor端执行时打印
    rdd1.foreach(x => println(x + "---" + Thread.currentThread().getName))
  }

  /*
      foreach
   */
  @Test
  def test14(): Unit = {
    val list = List(1, 2, 3, 4, 5)
    val rdd1 = sc.makeRDD(list, 2)
    //sum运行在Driver
    var sum = 0
    /*
        foreach和外部变量sum形成闭包,sum在Driver端，rdd中的计算逻辑，即算子会被封装到Task发送到不同的Executor，进行分布式运算
        即foreach和sum的副本会被序列化，发送到Executor所在的机器，Driver端sum不会改变

     */
    rdd1.foreach(x => {
      sum += x
      println(sum)
    })
    println(sum)
  }

  /*
      save
   */
  @Test
  def test13(): Unit = {
    val list1 = List(1, 2, 3, 4, 5)
    val list2 = List((1, 1), (2, 1), (3, 1), (1, 1), (2, 1))

    val rdd1: RDD[Int] = sc.makeRDD(list1, 3)
    val rdd2: RDD[(Int, Int)] = sc.makeRDD(list2, 3)

    //rdd1.saveAsTextFile("output")
    //rdd1.saveAsObjectFile("output")

    rdd2.saveAsSequenceFile("output") //必须为k-v结构
  }

  /*
     countByValue : 不要求RDD的元素是key-value类型，并将每个元素看成一个整体的value，统计value出现的次数
   */
  @Test
  def test12(): Unit = {
    val list1 = List(1, 2, 3, 4, 3, 2, 1)

    val list2 = List((1, 1), (2, 1), (3, 1), (1, 1), (2, 1))
    val rdd1 = sc.makeRDD(list1, 2)
    val rdd2 = sc.makeRDD(list2, 2)

    val res1 = rdd1.countByValue()
    val res2 = rdd2.countByValue()

    println(res1)
    println(res2)


  }

  /*
      countByKey:  基于key的数量进行统计，要求RDD的元素是key-value类型
   */
  @Test
  def test11(): Unit = {
    val list = List((1, 1), (2, 1), (3, 1), (1, 1), (2, 1))
    val rdd1 = sc.makeRDD(list, 2)
    val count = rdd1.countByKey()
    println(count) //Map(2 -> 2, 1 -> 2, 3 -> 1) 返回每个键及键对应的数量
  }

  /*
      fold : 简化版aggregate。 要求 zeroValue必须和RDD中的元素类型一致，且分区间和分区内运算逻辑一致
      def fold(zeroValue: T)(op: (T, T) => T): T
   */
  @Test
  def test10(): Unit = {
    val list = List(1, 2, 3, 4, 5)
    val rdd1 = sc.makeRDD(list, 2)
    val res = rdd1.fold(2)(_ + _)
    println(res)
  }

  /*
      def aggregate[U: ClassTag](zeroValue: U)(seqOp: (U, T) => U, combOp: (U, U) => U): U

      aggregate:  和aggregateByKey类似，不要求数据是KEY-VALUE，zeroValue既会在分区内使用，还会在分区间合并时使用
   */
  @Test
  def test9(): Unit = {
    val list = List(1, 2, 3, 4, 5)
    val rdd1 = sc.makeRDD(list, 2)
    val res = rdd1.aggregate(2)(_ + _, _ + _)
    println(res)

  }

  // takeOrdered : 先排序，再取前n
  @Test
  def test8(): Unit = {
    val list = List(9, 2, 3, 4, 5, 6)
    val rdd1 = sc.makeRDD(list, 2)
    val arr = rdd1.takeOrdered(2)
    println(arr.mkString(","))


  }

  // take: 取RDD中的前n个元素 ,先从0号区取，不够再从1号区
  @Test
  def test7(): Unit = {
    val list = List(1, 2, 3, 4, 5, 6)
    val rdd1: RDD[Int] = sc.makeRDD(list, 2)
    val arr = rdd1.take(4)
    println(arr.mkString(","))
  }


  // first : 取RDD中的第一个元素   没有对RDD重新排序，0号区的第一个
  @Test
  def test6(): Unit = {
    val list = List(1, 2, 3, 4, 5, 6)
    val rdd1 = sc.makeRDD(list, 2)
    val res = rdd1.first()
    println(res)
  }

  // count ： 统计RDD中元素的个数
  @Test
  def test5(): Unit = {
    val list = List(1, 2, 3, 4)
    val rdd1 = sc.makeRDD(list, 2)
    println(rdd1.count())
  }

  /*
      foreach
   */
  @Test
  def test4(): Unit = {
    val list = List(1, 2, 3, 4)
    val rdd1 = sc.makeRDD(list, 1)

    // collect 将运算结果收集到driver端，在driver端遍历结算结果，打印
    // 如果计算的结果数据量大，driver端就OOM
    //rdd1.foreach(x=>if(x % 2 == 1)println(x))
    rdd1.collect().foreach(println)
  }

  /*
      reduce
   */
  @Test
  def test3(): Unit = {
    val list = List(1, 2, 3, 4)
    val rdd = sc.makeRDD(list, 1)
    println(rdd.reduce(_ + _))
  }


  /*
      RangePartitioner在抽样时,将抽样的结果进行collect(),触发行动算子
      注意；new RangePartitioner时，传入的partitons如果 <=1,则不会触发

      sortBy在运行时，也会触发了行动算子，sortBy本身是一个转换算子
   */
  @Test
  def test2(): Unit = {
    val list = List(1, 2, 3, 4)
    val rdd1 = sc.makeRDD(list, 2)

    val rdd2 = rdd1.map(e => {
      println(e)

      (e, 1)
    })

    rdd2.sortBy(_._1)

    //val partitioner = new RangePartitioner(1,rdd2)

  }

  //测试行动算子和转换算子
  @Test
  def test1(): Unit = {
    val list = List(1, 2, 3, 4)
    val rdd1 = sc.makeRDD(list, 2)

    val rdd2 = rdd1.map(e => {
      println(e)
      e
    })

    rdd2.collect()

  }


  val sc: SparkContext = new SparkContext(new SparkConf().setMaster("local").setAppName("MyApp"))
  val path = new Path("output")

  @Before
  def init(): Unit = {
    val fs = FileSystem.get(new Configuration())

    if (fs.exists(path)) {
      fs.delete(path, true)
    }
  }

  @After
  def stop() = {
    sc.stop()
  }

}
