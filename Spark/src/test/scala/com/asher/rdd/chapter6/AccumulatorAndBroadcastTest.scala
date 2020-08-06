package com.asher.rdd.chapter6

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.util.{AccumulatorV2, DoubleAccumulator, LongAccumulator}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

import scala.collection.mutable

/**
 * 累加器:  场景： 计算，累加
 * 可以给累加器起名称，可以在WEB UI查看每个Task中累加器的值，以及汇总后的值！
 * 官方只提供了数值类型(Long，Double)的累加器，用户可以通过实现接口，自定义！
 *
 * 获取官方累加器：  SparkContext.LongAccomulator
 *
 * Task：  调用add进行累加，不能读取值！
 * Driver:  调用value获取累加的值
 *
 * 自定义：  实现AccumulatorV2
 * 必须实现的核心方法：
 * ①add  : 累加值
 * ②reset ： 重置累加器到0
 * ③merge:  合并其他累加器到一个累加器
 *
 * 创建： new
 * 还需要调用 sparkContext.register() 注册
 * 调用行动算子，才会实现真正的累加，不会重复累加，即便是task重启！
 *
 * 累加器在序列化到task之前： copy()返回当前累加器的副本 ---> reset()重置为0 ---->isZero(是否归0)
 * 只有在isZero返回true，此时才会执行序列化！
 * false就报错！
 * 目的为了保证累加器技术的精确性！
 *
 * wordcount(单词，1)(单词，1) 可以使用累加器解决！
 * 好处：避免了shuffle，在Driver端聚合！
 *
 * 注意：  序列化到task之前，要返回一个归0累加器的拷贝！
 *
 *
 * 广播变量：  允许将广播变量发给给每一个机器，而不是拷贝拷贝给每一个task!
 *
 * 使用场景：  需要在多个task中使用共同的大的只读的数据集！
 * 作用：     节省网络传输消耗！
 *
 */

//第一个泛型，传入的类型，第二个泛型，保存累加结果的类型
class WordCountAccumulator extends AccumulatorV2[String, mutable.Map[String, Int]] {
  //保存累加结果到集合
  private var wordMap: mutable.Map[String, Int] = mutable.Map()

  //判断结果集是否为空
  override def isZero: Boolean = wordMap.isEmpty

  //复制
  override def copy(): WordCountAccumulator = new WordCountAccumulator()

  //重置归零
  override def reset(): Unit = wordMap.clear()

  //累加
  override def add(v: String): Unit = {
    wordMap.put(v, wordMap.getOrElse(v, 0) + 1)
  }

  //合并其它累加器的值到一个累加器
  override def merge(other: AccumulatorV2[String, mutable.Map[String, Int]]): Unit = {
    val otherMap = other.value
    for ((word, count) <- otherMap) {
      wordMap.put(word, wordMap.getOrElse(word, 0) + count)
    }
  }

  //获取最终累加的值
  override def value: mutable.Map[String, Int] = wordMap
}

class AccumulatorAndBroadcastTest {

  /*
      广播变量
   */
  @Test
  def test5(): Unit = {
    val list1 = List(1, 2, 3, 4, 5, 6, 7, 8)
    val list2 = List(5, 6, 7, 8)

    //疯转list2为广播变量
    val list2Bc = sc.broadcast(list2)

    val rdd1 = sc.makeRDD(list1, 4)

    //过滤，使用广播变量
    val rdd3 = rdd1.filter(x => list2Bc.value.contains(x))
    rdd3.saveAsTextFile("output")

  }


  /*
       广播变量的引入
   */
  @Test
  def test4(): Unit = {
    val list1 = List(1, 2, 3, 4, 5, 6, 7, 8)

    /*
        此时，list2要以副本的方式发送到对应Executor的节点，
        如果两个Task在一个Executor上，也要为每个Task发送一份，相当于同一台机器发了两份
        如果数据量很大，将大大降低性能

        解决：用广播变量，每台起Executor的机器只发送一份副本
     */
    val list2 = List(5, 6, 7, 8)

    val rdd1 = sc.makeRDD(list1, 2)

    //list2是一个闭包变量，需要复制副本到每一个Task
    rdd1.filter(x=>list2.contains(x))


  }


  /*
      使用自定义累加器
      计算wordcount
   */
  @Test
  def test3(): Unit = {
    val list = List("hello", "world", "java", "scala", "python", "java", "scala")

    val rdd1 = sc.makeRDD(list, 1)

    val acc = new WordCountAccumulator()

    sc.register(acc)

    rdd1.foreach(e => acc.add(e))

    println(acc.value)


  }

  /*
      使用累加器获取累加值
      系统提供了 LongAccumulator、 DoubleAccumulator两种累加器
   */
  @Test
  def test2(): Unit = {
    val list = List(1, 2, 3, 4)

    //不要自己new，从sc获取官方提供的累加器
    val acc = sc.longAccumulator("sumAcc")
    val rdd1 = sc.makeRDD(list, 1)

    acc.add(10)
    //闭包，将累加器以副本的形式发给Task，在此之前，先copy--reset--iszero(true)--然后进行累加
    rdd1.foreach(x => acc.add(x)) //执行完后返回累加器结果给Driver

    //将Task中累加结果累加到Driver
    println(acc.value) //打印Driver端累加结果

  }


  /*
      引入案例
   */
  @Test
  def test1(): Unit = {
    val list = List(1, 2, 3, 4)
    val rdd1 = sc.makeRDD(list, 1)

    //sum形成闭包
    var sum = 0
    rdd1.foreach(sum += _)

    //sum在Driver，为0
    println(sum)
  }


  val sc: SparkContext = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("MyApp"))
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
