package com.asher.sql.chapter8

import java.util.Properties

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.junit._

case class  MyUser(id: Int,name:String)
class DataInputAndOutPutTest {

  /*
        写
   */
  @Test
  def test7() : Unit ={

    import spark.implicits._

    val users = List(MyUser(7,"jack1"), MyUser(8,"jack2"), MyUser(9,"jack3"))

    val ds: Dataset[MyUser] = users.toDS()

    ds.write
      .option("url","jdbc:mysql://localhost:3306/mydb")
      .option("user","root")
      .option("password","root")
      .option("dbtable","user")
      .mode("append")
      .format("jdbc").save()

  }

  /*
        参数可以参考：JDBCOptions
   */
  @Test
  def test6() : Unit ={

    //通用
    val df: DataFrame = spark.read
      .option("url","jdbc:mysql://localhost:3306/mydb")
      .option("user","root")
      .option("password","root")
      // 查询全表
      //.option("dbtable","user")
      //  query和dbtable 互斥
      // 自定义查询
      .option("query","select * from user where id > 3")
      .format("jdbc").load()

    df.show()

  }

  /*
      从关系型数据库中读写数据
            ①配置驱动
   */
  @Test
  def test5() : Unit ={
    val properties = new Properties()
    properties.put("user","root")
    properties.put("password","root")

    //专用
    val df2: DataFrame = spark.read.jdbc("jdbc:mysql://localhost:3306/mydb","user",properties)

    //全表查询
    df2.show()

    df2.createTempView("tmp_user")

    import spark.sql
    //只查询部分数据
    sql("select * from tmp_user where id > 3").show()
  }


  /*
      不标准的csv: 在读取时，需要添加参数
          sep: csv文件中的分隔符
   */
  @Test
  def test4() : Unit ={

    val df2: DataFrame = spark.read
      .option("sep",":")
      .option("header",true)
      .csv("input/mycsv.csv")

    df2.show()

    df2.write
      .option("sep",",")
      .option("header",false)
      .mode("append").csv("output4")

  }

  /*
      CSV:  逗号分割的数据集，每行数据的字段都使用,分割
      tsv:  使用\t分割的数据集
   */
  @Test
  def test3() : Unit ={

    //通用的读取
    val df: DataFrame = spark.read.format("csv").load("input/mycsv.csv")
    // df.show()

    //专用
    val df2: DataFrame = spark.read.csv("input/mycsv.csv")
    df2.show()
  }

  @Test
  def test2() : Unit ={

    //省略建表的过程，直接指定数据源
    spark.sql("select * from json.`input/employees.json`").show()

  }

  /*
      通用的读取
            在spark中默认支持读取和写出的文件格式是parquet格式！
            Parquet(snappy)
   */
  @Test
  def test1() : Unit ={

    import spark.implicits._

    //通用方法，可以通过参数改变读取文件的类型
    val df: DataFrame = spark.read.format("json").load("input/employees.json")
    //df.show()

    //等价于  专用读取JSON方法
    val df2: DataFrame = spark.read.json("input/employees.json")
    // df2.show()

    val df3: DataFrame = df2.select('name, 'salary + 1000 as("newSalary"))
    //df3.show()

    //以通用的API，JSON格式写出
    //df3.write.format("json").save("output2")

    //专用方法
    //df3.write.mode("append").json("output3")

  }

  val spark: SparkSession = SparkSession.builder().master("local[*]").appName("df").getOrCreate()

  @After
  def close()={
    spark.stop()
  }

  /*
      parquet
   */
  @Test
  def test0(): Unit = {
    val df = spark.read.load("F:/Develop/Spark/spark-3.0.0-bin-hadoop3.2/examples/src/main/resources/users.parquet")

    df.show

    var df2 = spark.read.json("input/employees.json")
    //保存为parquet格式
    df2.write.mode("append").save("output/parquet")

  }
}
