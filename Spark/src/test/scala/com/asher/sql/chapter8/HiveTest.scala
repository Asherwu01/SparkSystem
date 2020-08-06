package com.asher.sql.chapter8

import org.apache.spark.sql.SparkSession
import org.junit._

class HiveTest {

  /*
     内置hive
   */
  @Test
  def test1(): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("sql")
      .getOrCreate()

    //spark.sql("create database mydb1").show()
    //spark.sql("create table p1(name string)").show()//本地建表不支持
    spark.sql("show tables").show()
    spark.sql("show databases").show()

    spark.stop()
  }

  /*
      外置hive
   */
  @Test
  def test2(): Unit = {
    System.setProperty("HADOOP_USER_NAME", "asher")

    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("sql")
      .enableHiveSupport()
      .config("spark.sql.warehouse.dir", "hdfs://hadoop102:9820/user/hive/warehouse")
      .getOrCreate()

    //spark.sql("show databases").show()
    //spark.sql("show tables").show()
    //spark.sql("select * from person1").show()
    //spark.sql("create table p1(name string)").show()
    //spark.sql("create table p2(name string)").show()
    spark.sql("insert into p2 values(\"tom\")")
    //spark.sql("create database mydb1").show()


    spark.stop()
  }

  @Test
  def test3(): Unit = {

  }

}
