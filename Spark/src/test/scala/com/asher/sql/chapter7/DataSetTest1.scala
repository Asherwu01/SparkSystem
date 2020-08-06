package com.asher.sql.chapter7

import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.junit.{After, Before, Test}

case class User(name: String, age: Int)
case class User1(name: String, salary: Double)
class DataSetTest1 {
  val sparkSession: SparkSession = SparkSession.builder().master("local[*]").appName("df").getOrCreate()

  @Before
  def init() = {
  }

  @After
  def stop() = {
    sparkSession.stop()
  }


  /*
       创建DS
       implicits对象中提供了 list-->DF/DS
   */
  @Test
  def test1(): Unit = {
    import sparkSession.implicits._
    val list = List(1, 2, 3, 4)
    val ds = list.toDS()
    ds.show()
  }

  /*
      rdd --> ds
   */
  @Test
  def test2(): Unit = {
    import sparkSession.implicits._
    val rdd = sparkSession.sparkContext.makeRDD(List(1, 2, 3, 4), 2)

    val ds = rdd.toDS()
    ds.show()
  }

  /*
      ds --> rdd
   */
  @Test
  def test3(): Unit = {
    import sparkSession.implicits._
    val list = List(1, 2, 3, 4)
    val ds = list.toDS()

    val rdd = ds.rdd

    rdd.foreach(println)
  }

  /*
      ds --> df
   */
  @Test
  def test4(): Unit = {
    import sparkSession.implicits._
    val list = List(1, 2, 3, 4)
    val ds = list.toDS()

    val df = ds.toDF("num")
    df.show()
  }

  /*
      df --> ds
   */
  @Test
  def test5(): Unit = {
    import sparkSession.implicits._
    val list = List(1, 2, 3, 4)
    val df = list.toDF()

    val ds = df.as[Integer]
    ds.show()
  }

  /*
      常用样例类创建 DS
   */
  @Test
  def test6(): Unit = {
    import sparkSession.implicits._
    val ds: Dataset[User] = List(User("jack1", 20), User("jack2", 20), User("jack3", 20)).toDS()

    ds.show()

  }

  /*
      由DF转DS

      Encoder:  将一个Java的对象，转为DF或DS结构时，需要使用的编码器！
                在创建DS或DF时，系统会自动提供隐式的Encoder自动转换！
                导入 sparkSession.implicits._
   */
  @Test
  def test7(): Unit = {
    import sparkSession.implicits._
    val list = List(User("jack1", 20), User("jack2", 20), User("jack3", 20))
    val df = list.toDF()

    //DataFrame[Row]  == DataSet[User]
    val ds = df.as[User]
    ds.show()
  }

  /*
      DF和DS的区别
   */
  @Test
  def test8() : Unit ={

    import sparkSession.implicits._

    val df: DataFrame = sparkSession.read.json("input/employees.json")
    //df.printSchema()

    //使用df读取数据的第一列  DataSet[Row]
    //val ds = df.map(row => row.getInt(0)) //ds中存储的是Any，abstract，报错
    //ds.printSchema()

    // 在运行时出错，
    //df.show()
    //df.foreach(e=>println(e))

    // 使用DS，强类型，在编译时就进行检查
    val ds = df.as[User1]//User1是和employee中字段对应的样例类
    val ds2 = ds.map(u=>u.name)
    ds2.show()

  }

}


