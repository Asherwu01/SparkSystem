package com.asher.rdd.chapter5

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

/**
 * 持久化： 将数据进行持久保存，一般持久化指将数据保存到磁盘！
 */
class PersistenceTest {
  /*
        checkpoint :  将部分阶段运行的结果，存储在持久化的磁盘中！
           作用： 将RDD保存到指定目录的文件中！
                 切断当前RDD和父RDD的血缘关系
                 在Job执行完成后才开始运行！
                 强烈建议将checkpoint的RDD先缓存，不会造成重复计算！

                 checkpoint也会提交一个Job，执行持久化！
                 在第一个行动算子执行完成后，提交Job，运行！
   */
  @Test
  def test3(): Unit = {
    val list = List(1, 2, 3, 4)

    sc.setCheckpointDir("checkpoint")

    val rdd1 = sc.makeRDD(list, 2)
    val rdd2 = rdd1.map(x => {
      println(x + "  map" + Thread.currentThread().getName)
      x
    })

    val rdd3 = rdd2.map(x => {
      println(x + "  map" + Thread.currentThread().getName)
      x
    })

    val rdd4 = rdd3.map(x => {
      println(x + "  map   " + Thread.currentThread().getName)
      x
    })

    println("-----------ck之前的血缘关系--------------")
    println(rdd4.toDebugString)

    println("-----------ck之后的血缘关系--------------")
    //指定缓存级别，checkpoint，缓存到磁盘
    rdd4.checkpoint() // checkpoint也会提交一个Job，执行持久化
    println(rdd4.toDebugString)
    rdd4.cache()//第一个job执行时，先缓存，checkpoint提交job就会走缓存
    rdd4.collect()
    rdd4.collect()

    Thread.sleep(1000000)

  }

  /*
     缓存：
          提高对重复对象查询的效率
          查询缓存的过程：
                    ①到缓存中查询，有就返回，没有就查数据库
                    ②缓存一般都使用内存
                    ③缓存都有回收策略
                    ④缓存有失效的情况 (更新了缓存中的对象)
      缓存不会改变血缘关系！
      如果在计算的阶段中产生Shuffle，shuffle之后的数据会自动缓存！
      在执行时，发现重复执行shuffle之前的阶段，此时会跳过之前阶段的执行，直接从缓存中获取数据！

  */
  @Test
  def test2(): Unit = {
    val list = List(1, 2, 3, 4)
    val rdd1: RDD[Int] = sc.makeRDD(list, 2)
    val rdd2 = rdd1.map(x => {
      println(x + "   map")
      x -> 1
    })

    val rdd3 = rdd2.reduceByKey(_ + _) //job1,在执行行动算子时，会缓存，因为shuffle

    rdd3.collect()

    //job2
    rdd3.saveAsTextFile("output") //此次job没有打印
    Thread.sleep(100000000)
  }


  @Test
  def test1(): Unit = {
    val list = List(1, 2, 3, 4)

    val rdd1: RDD[Int] = sc.makeRDD(list, 2) //ParallelCollectionRDD

    val rdd2: RDD[Int] = rdd1.map(x => { //MapPartitionsRDD
      println(x + "   map")
      x
    })

    // 第一个行动算子执行时，将结果放入缓存,生效时间是下面第一个行动算子执行时。
    //rdd2.cache()
    //rdd2.persist()//等价cache(),只不过数据默认缓存在磁盘，可以指定
    rdd2.collect()

    //rdd2.cache()//下面行动算子执行时才收集
    //println("------------------------")
    //rdd2.collect()

    println("=====================缓存之前的血缘关系================")
    println(rdd2.toDebugString)

    println("=====================缓存之后的血缘关系================")

    val rdd3 = rdd2.persist(StorageLevel.MEMORY_ONLY)
    println(rdd3.toDebugString)

    //从缓存取出rdd2
    rdd3.saveAsTextFile("output")
    Thread.sleep(10000000)

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
