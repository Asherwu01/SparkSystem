package com.asher.rdd.chapter4

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._
/*
 * join
 * 如果key存在不相等呢？
 *
 * leftOuterJoin
 *
 * cogroup
 */
case class Person(name:String,age:Int) extends Ordered[Person] {
  override def compare(that: Person): Int = this.name.compareTo(that.name)
}
case class Person1(name:String,age:Int)

/**
 *  key-value类型
 */
class KeyValueRDDTest2 {
  @Test
  def test10(): Unit = {
    val list1 = List((1, "a"), (2, "b"), (3, "c"),(5, "e"),(1, "aa"), (2, "bb"))
    val list2 = List((1, "a1"), (2, "b1"), (3, "c1"),(4, "d1"),(3, "c11"),(4, "d11"))

    val rdd1 = sc.makeRDD(list1,2)
    val rdd2 = sc.makeRDD(list2,2)
    val rdd3 = rdd1.leftOuterJoin(rdd2)
    rdd3.foreach(println)
  }

  /*
      cogroup: 先每个RDD内部，根据key进行聚合，将相同key的value聚合为Iterable，再在rdd之间，根据相同key聚合
   */
  @Test
  def test9(): Unit = {
    val list1 = List((1, "a"), (2, "b"), (3, "c"),(5, "e"),(1, "aa"), (2, "bb"))
    val list2 = List((1, "a1"), (2, "b1"), (3, "c1"),(4, "d1"),(3, "c11"),(4, "d11"))

    val rdd1 = sc.makeRDD(list1,2)
    val rdd2 = sc.makeRDD(list2, 2)

    val rdd3 = rdd1.cogroup(rdd2)
    rdd3.saveAsTextFile("output")

  }


  /*
       rightOuterJoin
   */
  @Test
  def test8(): Unit = {
    val list1 = List((1, "a"), (2, "b"), (3, "c"),(5, "e"),(1, "aa"), (2, "bb"))
    val list2 = List((1, "a1"), (2, "b1"), (3, "c1"),(4, "d1"),(3, "c11"),(4, "d11"))
    val rdd1 = sc.makeRDD(list1, 2)
    val rdd2 = sc.makeRDD(list2, 2)
    val rdd3 = rdd1.fullOuterJoin(rdd2,1)
    rdd3.saveAsTextFile("output")
  }

  /*
       rightOuterJoin
   */
  @Test
  def test7(): Unit = {
    val list1 = List((1, "a"), (2, "b"), (3, "c"),(5, "e"),(1, "aa"), (2, "bb"))
    val list2 = List((1, "a1"), (2, "b1"), (3, "c1"),(4, "d1"),(3, "c11"),(4, "d11"))
    val rdd1 = sc.makeRDD(list1, 2)
    val rdd2 = sc.makeRDD(list2, 2)
    val rdd3 = rdd1.rightOuterJoin(rdd2,1)
    rdd3.saveAsTextFile("~")
  }

  /*
       leftOuterJoin
   */
  @Test
  def test6(): Unit = {
    val list1 = List((1, "a"), (2, "b"), (3, "c"),(5, "e"),(1, "aa"), (2, "bb"))
    val list2 = List((1, "a1"), (2, "b1"), (3, "c1"),(4, "d1"),(3, "c11"),(4, "d11"))
    val rdd1 = sc.makeRDD(list1, 2)
    val rdd2 = sc.makeRDD(list2, 2)
    val rdd3 = rdd1.leftOuterJoin(rdd2,1)
    rdd3.saveAsTextFile("output")
  }

  /*
      join : 不同RDD中，将key相同的value进行关联
          有shuffle
   */
  @Test
  def test5(): Unit = {
    val list1 = List((1, "a"), (2, "b"), (3, "c"),(5, "e"),(1, "aa"), (2, "bb"))
    val list2 = List((1, "a1"), (2, "b1"), (3, "c1"),(4, "d1"),(3, "c11"),(4, "d11"))
    val rdd1 = sc.makeRDD(list1, 2)
    val rdd2 = sc.makeRDD(list2, 2)

    val rdd3 = rdd1.join(rdd2)
    rdd3.saveAsTextFile("output")

  }

  /*
      针对对象属性排序
   */
  @Test
  def test4(): Unit = {
    val list = List(Person1("jack",20), Person1("jack",21), Person1("tom",10), Person1("marry",20))
    val rdd1 = sc.makeRDD(list,2)
    val rdd2 = rdd1.map(p => p.age -> p)
    val rdd3 = rdd2.sortByKey(true, 1)
    rdd3.saveAsTextFile("output")
  }


  /*
      自定义对象，排序，提供Ordering对象
   */
  @Test
  def test3(): Unit = {
    implicit val ordering:Ordering[Person1] = new Ordering[Person1] {
      override def compare(x: Person1, y: Person1): Int = x.age-y.age
    }

    val list = List(Person1("jack",20), Person1("jack",21), Person1("tom",10), Person1("marry",20))
    val rdd1 = sc.makeRDD(list)

    val rdd2 = rdd1.map(_ -> 1)
    val rdd3 = rdd2.sortByKey(true, 1)
    rdd3.saveAsTextFile("output")
  }

  /*
      对自定义对象，排序 继承Ordered
   */
  @Test
  def test2(): Unit = {
    val list = List(Person("jack",20), Person("jack",21), Person("tom",10), Person("marry",20))
    val rdd1 = sc.makeRDD(list)

    val rdd2 = rdd1.map(_ -> 1)
    val rdd3 = rdd2.sortByKey(true, 1)
    rdd3.saveAsTextFile("output")

  }

  /*
    sortByKey: 根据key进行排序
      def sortByKey(ascending: Boolean = true, numPartitions: Int = self.partitions.length): RDD[(K, V)]

      要求排序的key的类型，必须是可以排序的Ordered类型！
   */
  @Test
  def test1(): Unit = {
    val list = List(1, 2, 3, 4)
    val rdd1 = sc.makeRDD(list,2)
    val rdd2 = rdd1.map(_ -> 1)
    val rdd3 = rdd2.sortByKey(false, 1)
    rdd3.saveAsTextFile("output")
  }



  val sc: SparkContext = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("MyApp"))
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
