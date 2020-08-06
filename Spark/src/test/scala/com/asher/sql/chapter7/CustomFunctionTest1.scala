package com.asher.sql.chapter7

import org.apache.spark.sql.{Encoder, Encoders, Row, SparkSession}
import org.apache.spark.sql.expressions.{Aggregator, MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, StructField, StructType}
import org.junit.{After, Before, Test}

/**
 * UDF: 一进一出
 * UDAF:多进一出
 */
class CustomFunctionTest1 {
  val sparkSession: SparkSession = SparkSession.builder().master("local[*]").appName("df").getOrCreate()

  @Before
  def init() = {
  }

  @After
  def stop() = {
    sparkSession.stop()
  }

  /*
      UDF:
          ①创建函数
          ②注册函数
          ③使用
   */
  @Test
  def test1(): Unit = {
    import sparkSession.implicits._

    // 定义并注册函数
    val say = sparkSession.udf.register("say", (name: String) => {
      "Mr." + name
    })

    //使用
    val df = sparkSession.read.json("input/employees.json")
    df.createTempView("emp")

    val df1 = sparkSession.sql(("select say(name) from emp"))
    df1.show()
  }

  /*
      老版本 UDAF: 继承UserDefinedAggregateFunction实现
      模拟 sum()
      ①创建函数
      ②注册函数
      ③使用函数
   */
  @Test
  def test2(): Unit = {
    import sparkSession.implicits._

    // 创建函数对象
    val mySum = new MySum

    // 注册
    sparkSession.udf.register("mySum",mySum)

    //使用
    val df = sparkSession.read.json("input/employees.json")
    df.createTempView("emp")

    val df1 = sparkSession.sql(("select sum(salary) from emp"))
    df1.show()
  }



  /*
     新版本 UDAF: Aggregator[IN, BUF, OUT]
         IN: 输入的类型   User1
         BUF： 缓冲区类型   MyBuffer
         OUT：输出的类型  Double

         和DataSet配合使用，使用强类型约束！

       求平均值：    sum /  count

  */
  @Test
  def test3(): Unit = {
    import sparkSession.implicits._

    //df准备
    val df = sparkSession.read.json("input/employees.json")
    df.createTempView("emp")
    val ds = df.as[User1]

    // 创建函数对象
    val myAvg = new MyAvg

    // 将UDAF转成一个列名
    val avgColumn = myAvg.toColumn

    // DSL风格查询
    ds.select(avgColumn).show()

  }

}


class MySum extends UserDefinedAggregateFunction {
  //输入数据的结构信息
  override def inputSchema: StructType = StructType(StructField("input",DoubleType)::Nil)

  //buffer:缓冲区，用于保持中间的临时结果
  //bufferSchema:缓冲区的类型
  override def bufferSchema: StructType = StructType(StructField("sum",DoubleType)::Nil)

  //最终返回的结果类型
  override def dataType: DataType = DoubleType

  //是否是确定性的，传入相同的输入，是否总是会有相同的输出
  override def deterministic: Boolean = true

  //初始化缓冲区，赋值zero value  MutableAggregationBuffer extends Row
  override def initialize(buffer: MutableAggregationBuffer): Unit = buffer(0)=0.0

  //将输入的数据，进行计算，更新buffer  分区内运算
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit =
    buffer(0) = buffer.getDouble(0)+input.getDouble(0)

  // 分区间合并结果  将buffer2的结果累加到buffer1
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit =
    buffer1(0) = buffer1.getDouble(0)+buffer2.getDouble(0)

  //返回最后的结果集
  override def evaluate(buffer: Row): Any = buffer(0)
}

class MyAvg extends Aggregator[User1,MyBuff,Double] {
  //初始化缓冲区
  override def zero: MyBuff = MyBuff(0.0,0)

  // 分区内聚合
  override def reduce(b: MyBuff, a: User1): MyBuff = {
    b.sum += a.salary
    b.count += 1
    b
  }

  // 分区间聚合，将b2的值聚合到b1上
  override def merge(b1: MyBuff, b2: MyBuff): MyBuff = {
    b1.sum += b2.sum
    b1.count += b2.count
    b1
  }

  //返回结果
  override def finish(reduction: MyBuff): Double = reduction.sum / reduction.count

  // 获取buffer的Encoder类型  buffer是样例类，获取方式：ExpressionEncoder[样例类类型] 或 Encoders.product
  override def bufferEncoder: Encoder[MyBuff] = Encoders.product[MyBuff]

  // 最终返回结果的Encoder类型
  override def outputEncoder: Encoder[Double] = Encoders.scalaDouble
}

case class MyBuff(var sum:Double,var count:Int)