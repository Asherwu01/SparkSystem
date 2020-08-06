package com.asher.rdd.chapter3

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

/**
 * 单值类型RDD操作
 */
class SingleValueTest1 {

  /*
    创建rdd
   */
  @Test
  def test1() = {
    val list = List(1, 2, 4, 5)
    val rdd = sc.makeRDD(list, 3)
    val rdd1 = sc.parallelize(list, 3)
    rdd1.saveAsTextFile("output")
  }

  @Test
  def test2() = {
    val list = List(1, 2, 4, 5)
    val rdd = sc.makeRDD(list, 2)
    val rdd1 = rdd.map(_ + 1)
    rdd1.saveAsTextFile("output")
  }

  /*
      map
      def map[U: ClassTag](f: T => U): RDD[U]
        分区之间是并行（真正并行取决于cores）运算
        同一个分区内，一个元素执行完所有的转换操作后，才开始下一个元素！
   */
  @Test
  def test3():Unit = {
    val list = List(1, 2, 4, 5)
    val rdd1 = sc.makeRDD(list,2)
    val rdd2 = rdd1.map(x => {
      println(x+"执行了第一次map操作")
      x;
    }).map(x=>{
      println(x+"执行了第二次map操作")
      x
    })
    rdd2.collect()

  }

  // 小功能：从服务器日志数据apache.log中获取用户请求URL资源路径
  @Test
  def test4() = {
    val rdd1 = sc.textFile("input/apache.log",1)
    val rdd2 = rdd1.map(str => {
      val array = str.split(" ")
      array(6)
    })
    rdd2.collect().foreach(println)
  }


  /*
      mapPartitions :
        将一个分区作为一个整体，调用一次map函数，转换后生成新的分区集合！

      def mapPartitions[U: ClassTag](
      f: Iterator[T] => Iterator[U],
      preservesPartitioning: Boolean = false): RDD[U]

        和map的区别：  ①传入的函数不同，map将一个元素转为另一个元素， mapPartitions将一个集合变为另一个集合！
                     ② mapPartiion逻辑：  cleanedF(iter)    批处理
                        map逻辑：  iter.map(cleanF)         个体处理
                     ③map是全量处理：  RDD中有x个元素，返回的集合也有x个元素
                        mapPartition只要返回一个集合，进行过滤或添加操作！
                     ④ 本质是mapPartition是一个集合调用一次
                        在特殊场景，节省性能，例如将一个分区的数据，写入到数据库中
                     ⑤ map是将一个元素的所有转换操作运行结束后，再继续开始下一个元素！
                        mapPartition： 多个分区并行开始转换操作，一个分区的所有数据全部运行结束后，mapPartition才结束！
                            一旦某个分区中的元素没有处理完，整个分区的数据都无法释放！需要更大的内存！

         spark是分布式运算： 时刻分清Driver 和 Executor
                            Executor执行的是Task(封装了RDD的执行逻辑)
   */
  @Test
  def test5() = {
    val list = List(1, 2, 3, 4, 5)
    val rdd1 = sc.makeRDD(list, 2)
    val rdd2 = rdd1.mapPartitions(x => x.map(_ * 2))
    rdd2.saveAsTextFile("output")
  }

  /*
      获取每个数据分区的最大值
   */
  @Test
  def test6() = {
    val list = List(1, 2, 3, 4)
    val rdd1 = sc.makeRDD(list, 2)
    val rdd2 = rdd1.mapPartitions(x => List(x.max).iterator)
    println(rdd2.collect.mkString(","))
  }

  /*
      mapPartitionsWithIndex :
      def mapPartitionsWithIndex[U: ClassTag](
            f: (Int, Iterator[T]) => Iterator[U],
            preservesPartitioning: Boolean = false)
        执行逻辑  f(index, iter) : index是当前分区的索引
               ter是分区的迭代器
               将一个分区整体执行一次map操作，可以使用分区的index!
   */
  @Test
  def test7() = {
    val list = List(1, 2, 3, 4, 5)
    val rdd1 = sc.makeRDD(list, 2)
    val rdd2 = rdd1.mapPartitionsWithIndex((index, iter) => iter.map(e => index -> e))
    rdd2.saveAsTextFile("output")
  }

  // 获取第二个数据分区的数据
  @Test
  def test8():Unit = {
    val list = List(1, 2, 3, 4, 5)
    val rdd1 = sc.makeRDD(list, 2)
    val rdd2 = rdd1.mapPartitionsWithIndex((index, iter) => {
      if (index == 1) iter
      else Nil.iterator
    })
    rdd2.saveAsTextFile("output")
  }




  val sc: SparkContext = new SparkContext(new SparkConf().setMaster("local").setAppName("MyApp"))
  val path = new Path("output")
  @Before
  def init():Unit = {
    val fs = FileSystem.get(new Configuration())

    if (fs.exists(path)){
      fs.delete(path,true)
    }
  }

  @After
  def stop() = {
    sc.stop()
  }
}
