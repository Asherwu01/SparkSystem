package com.asher.rdd.chapter3

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

class SingleValueTest2 {

  /*
        flatMap :
          先map再扁平化。不会改变分区和分区逻辑！
          将List(List(1,2),3,List(4,5))进行扁平化操作
   */
  @Test
  def test1(): Unit = {
    val list = List(List(1, 2), 3, List(4, 5))
    val rdd1 = sc.makeRDD(list, 1)
    val rdd2 = rdd1.flatMap {
        case a: Int => List(a)
        case b: List[_] => b
      }
    /*if (e.isInstanceOf[Int]) {
        List(e)
      } else e.asInstanceOf[List[Int]]*/

    rdd2.saveAsTextFile("output")
  }

  /*
      glom():
      def glom(): RDD[Array[T]]
        将一个分区的所有元素合并到一个Array中
   */
  @Test
  def test2(): Unit = {
    val list = List(1, 2, 3, 4, 5, 6)
    val rdd1 = sc.makeRDD(list, 3)
    val rdd2 = rdd1.glom()
    val array = rdd2.collect
    array.foreach(e => println(e.mkString(",")))
  }

  /*
      计算所有分区最大值求和（分区内取最大值，分区间最大值求和）
   */
  @Test
  def test3(): Unit = {
    val list = List(1, 2, 3, 4, 5, 6)
    val rdd1 = sc.makeRDD(list, 3)
    val rdd2 = rdd1.glom().map(arr => arr.max)
    val res = rdd2.reduce(_ + _)
    println(res)
  }


  /*
      filter ： 过滤！ 不会改变分区数！
   */
  @Test
  def test4(): Unit = {
    val list = List(1, 2, 3, 4, 5, 6)
    val rdd1 = sc.makeRDD(list, 3)
    val rdd2 = rdd1.filter(_ % 2 == 1)
    val array = rdd2.collect()
    array.foreach(println)
  }

  //小功能：从服务器日志数据apache.log中获取2015年5月17日的请求路径
  @Test
  def test5(): Unit = {
    val rdd1 = sc.textFile("input/apache.log")
    val rdd2 = rdd1.filter(line => line.contains("17/05/2015"))
    val rdd3 = rdd2.map(line => line.split(" ")(6))
    rdd3.saveAsTextFile("output")
  }


  /*
      groupby : 分组。
                 使用传入的f函数对分区中的T类型计算，计算后的类型可以和T不一致！
                 将计算后的结果进行分组，以计算后的结果作为key,将对应的T作为value分组！
                 有shuffle!
     def groupBy[K](
      f: T => K,
      numPartitions: Int)(implicit kt: ClassTag[K]): RDD[(K, Iterable[T])]

      只要看到一个算子，提供了numPartitions：Int参数，代表这个算子可能会产生shuffle!
      groupby分组后，默认分区数不变，数据会被重新分区！

   */
  @Test
  def test6(): Unit = {
    val list = List(1, 2, 3, 4, 5, 6,7,8,4,7)
    val rdd1 = sc.makeRDD(list, 4)
    //val rdd2 = rdd1.groupBy(x => x)
    //rdd2.saveAsTextFile("output")

    //手动指定分区,是k-v结构，还是默认分区器，HashPartitioner,只是改变了分区数
    val rdd2 = rdd1.groupBy(x => x,2)
    rdd2.saveAsTextFile("output")
  }

  /*
       将List("Hello", "hive", "hbase", "Hadoop")根据单词首写字母进行分组
   */
  @Test
  def test7(): Unit = {
    val list = List("Hello", "hive", "hbase", "Hadoop")
    val rdd1 = sc.makeRDD(list, 2)
    val rdd2 = rdd1.groupBy(e => e.head)
    rdd2.saveAsTextFile("output")

  }

  /*
     从服务器日志数据apache.log中获取每个时间段(不考虑日期)访问量。
   */
  @Test
  def test8(): Unit = {
    val rdd1 = sc.textFile("input/apache.log")
    val rdd2 = rdd1.map(e => e.split(" ")(3))
    val rdd3 = rdd2.map(_.split(":")(1))
    val rdd4 = rdd3.groupBy(x => x)
    val rdd5 = rdd4.map(e => e._1 -> e._2.size)
    rdd5.saveAsTextFile("output")
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
