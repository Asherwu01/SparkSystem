package com.asher.rdd.chapter1

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

class RDDTest1 {
  var conf: SparkConf = new SparkConf().setMaster("local").setAppName("MyApp")
  var sc: SparkContext = new SparkContext(conf)

  @Before
  def init():Unit = {
    val fs = FileSystem.get(new Configuration())
    val path = new Path("output")
    if (fs.exists(path)) fs.delete(path,true)
  }


  @Test
  def test1(): Unit = {
    val list = List(1, 2, 5, 6)
    val rdd1 = sc.makeRDD(list, 2)
    rdd1.saveAsTextFile("output")
  }

  @After
  def stop() = {
    sc.stop()
  }
}
