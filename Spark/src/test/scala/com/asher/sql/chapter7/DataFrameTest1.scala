package com.asher.sql.chapter7

import org.apache.spark.SparkConf
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}
import org.junit._

case class Person(name: String, salary: Double)

class DataFrameTest {

  val sparkSession: SparkSession = SparkSession.builder().master("local[*]").appName("df").getOrCreate()

  @Before
  def init() = {
  }

  @After
  def stop() = {
    sparkSession.stop()
  }

  /*
      创建SparkSession 方式一：
   */
  @Test
  def test1(): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("df")

    val session = SparkSession.builder().config(conf).getOrCreate()

    session.stop()
  }

  /*
      创建SparkSession 方式二：
   */
  @Test
  def test2(): Unit = {

    val session = SparkSession.builder().master("local[*]").appName("df").getOrCreate()

    session.stop()
  }


  /*
      创建DataFrame，DF-->RDD
      DF对RDD进行了封装，RDD是DF的一个成员属性
      DF.rdd

      type DataFrame = Dataset[Row]
   */
  @Test
  def test3(): Unit = {
    val df = sparkSession.read.json("input/employees.json")

    df.show()
    //df.createTempView("emp")
    //session.sql("select * from emp")

    //df --> rdd
    val rdd1 = df.rdd

    //val rdd2 = rdd1.map(row => row(0) -> row(1))
    //rdd2.foreach(println)

    rdd1.map({
      case a: Row => (a.getInt(0), a.getLong(1))
    }).foreach(println)

  }

  /*
        RDD --> DataFrame

        RDD出现的早，DF出现的晚，RDD在编写源码时，不会提供对DF转换！
        会后出现的DF，又希望RDD可以对DF提供转换，需要扩展RDD类的功能！

        scala为一个类提供扩展：
                ① 动态混入（非侵入式）
                ② 隐式转换  (非侵入式)   spark使用

        ①在SparkSession.class类中，声明了一个object:implicits。每创建一个SparkSession的对象，
          这个对象中，就包含一个名为implicits的对象.
        ②implicits extends SQLImplicits，在SQLImplicits，提供rddToDatasetHolder，可以将一个RDD转为DatasetHolder
        ③DatasetHolder提供toDS(), toDF()可以返回DS或DF
        结论： 导入当前sparksession对象中的implicits对象中的所有的方法即可！
                    import  sparkSession对象名.implicits._

                    RDD.toDF()----->DatasetHolder.toDF()

   */
  @Test
  def test4(): Unit = {
    val rdd1 = sparkSession.sparkContext.makeRDD(List(1, 2, 3, 4))

    import sparkSession.implicits._
    val df = rdd1.toDF()

    df.show()
  }

  /*
      使用样例类由RDD转DF
   */
  @Test
  def test5(): Unit = {
    import sparkSession.implicits._
    val list = List(Person("jack", 1), Person("tom", 2), Person("Bob", 3) ,Person("asher", 20))
    val rdd = sparkSession.sparkContext.makeRDD(list, 2)

    rdd.toDF("name","salary").show()
  }

  /*
      直接创建DF
        def createDataFrame(rowRDD: RDD[Row], schema: StructType)

            rowRDD: 要转换的RDD的类型，必须是RDD[Row]
            schema: 结构！ 在schema中指定一行数据Row对象，含有几个字段，以及他们的类型

                    StructType的创建：  StructType(List[StuctField])

           StructField: 结构中的一个字段

           case class StructField(
                name: String,   必传
                dataType: DataType,  必传
                nullable: Boolean = true,
                metadata: Metadata = Metadata.empty)
     */
    @Test
    def test6(): Unit = {
      val list = List(("jack", 1), ("tom", 2), ("Bob", 3) ,("asher", 20))

      val rdd1 = sparkSession.sparkContext.makeRDD(list, 2)
      val rdd2 = rdd1.map {
        case (name, age) => Row(name, age)
      }

      val structType = StructType(StructField("name", StringType) :: StructField("age", IntegerType) :: Nil)

      val df = sparkSession.createDataFrame(
        rdd2,
        structType
      )

      df.show()

    }

}
