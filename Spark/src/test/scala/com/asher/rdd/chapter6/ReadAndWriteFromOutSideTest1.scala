package com.asher.rdd.chapter6

import java.sql.{DriverManager, ResultSet}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.hbase.client.{Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{CellUtil, HBaseConfiguration}
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

class ReadAndWriteFromOutSideTest1 {

  /*写HBase
       输出格式： TableOutPutFormat: 将K-V写入到hbase表中
                             RecordWriter: [KEY, Mutation]
                                           [ImmutableBytesWritable  ,Put]
  */
  @Test
  def test10(): Unit = {

    val conf: Configuration = HBaseConfiguration.create()
    //写入哪个表
    conf.set(TableOutputFormat.OUTPUT_TABLE, "t1")

    // 设置让当前的Job使用TableOutPutFormat，指定输出的key-value类型
    val job: Job = Job.getInstance(conf)

    // 设置输出格式
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

    //指定输出的key-value类型
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setOutputValueClass(classOf[Put])

    //准备数据
    val list = List(("r2", "cf1", "name", "tom"), ("r3", "cf1", "name", "jack"))

    val rdd: RDD[(String, String, String, String)] = sc.makeRDD(list, 2)

    //在rdd中封装写出的数据
    val datas: RDD[(ImmutableBytesWritable, Put)] = rdd.map {

      case (rowkey, cf, cq, v) => {

        val key = new ImmutableBytesWritable()
        key.set(Bytes.toBytes(rowkey))

        val value = new Put(Bytes.toBytes(rowkey))
        value.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cq), Bytes.toBytes(v))

        (key, value)
      }

    }

    //datas写入到hbase
    datas.saveAsNewAPIHadoopDataset(job.getConfiguration)

  }



  /*
     读HBase
     输入格式： TableInputFormat: 读取一个表中的数据，封装为K-V对
               RecordReader:[ImmutableBytesWritable, Result]
               ImmutableBytesWritable: rowkey
               Result: 当前行的内容
   */
  @Test
  def test9(): Unit = {
    //获取Hbase相关的配置对象，通过配置对象，获取Hbase的地址
    val conf = HBaseConfiguration.create()

    //TableInputForma指定要读取HBase中的那张表
    conf.set(TableInputFormat.INPUT_TABLE, "t1")

    //读HBase表中的数据，返回RDD
    //通过指定的InputFormat，从Hadoop文件中获取数据
    val rdd = sc.newAPIHadoopRDD(conf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )

    val rdd1 = rdd.flatMap {
      case (rowKey, result) =>
        val cells = result.rawCells()
        for (cell <- cells) yield {
          val rk = Bytes.toString(CellUtil.cloneRow(cell))
          val cf = Bytes.toString(CellUtil.cloneFamily(cell))
          val cq = Bytes.toString(CellUtil.cloneQualifier(cell))
          val value = Bytes.toString(CellUtil.cloneValue(cell))

          //返回的内容
          rk + ":" + cf + ":" + cq + ":" + value
        }
    }

    rdd1.collect().foreach(println)

  }


  /*
      写入数据库
   */
  @Test
  def test8(): Unit = {
    val list = List("aa", "bb")

    val rdd = sc.makeRDD(list, 2)

    //将rdd中的数据写入数据库
    rdd.foreachPartition(it => {
      Class.forName("com.mysql.jdbc.Driver")
      val connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?useSSL=false", "root", "root")
      val ps = connection.prepareStatement("insert into user(name) values(?)")
      it.foreach {
        case name =>
          ps.setString(1, name)
          ps.execute()

      }
      ps.close()
      connection.close()

    })

  }


  /*
      从数据库中读取数据
      class JdbcRDD[T: ClassTag](
          sc: SparkContext,
          getConnection: () => Connection,
          sql: String,
          lowerBound: Long,
          upperBound: Long,
          numPartitions: Int,
          mapRow: (ResultSet) => T = JdbcRDD.resultSetToObjectArray _)
   */
  @Test
  def test7(): Unit = {
    val rdd = new JdbcRDD(
      sc,
      () => {
        Class.forName("com.mysql.jdbc.Driver")
        DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb?useSSL=false", "root", "root")
      },
      "select * from user where id >= ? and id <= ?",
      1,
      4,
      1,
      (rs: ResultSet) => {
        rs.getInt(1) + "---" + rs.getString(2)
      }
    )
    rdd.foreach(println)
  }

  /*
      从二进制文件中读数据
   */
  @Test
  def test6(): Unit = {
    val rdd1 = sc.sequenceFile[String, Int]("seq-file")
    rdd1.foreach(println)
  }

  /*
      写入二进制数据到文件
   */
  @Test
  def test5(): Unit = {
    val list = List("aa" -> 1, "bb" -> 2, "cc" -> 3)
    val rdd1 = sc.makeRDD(list)
    rdd1.saveAsSequenceFile("seq-file")
  }


  /*
      从文件中读 对象
   */
  @Test
  def test4(): Unit = {
    val rdd1 = sc.objectFile("object-file")
    rdd1.foreach(println)
  }

  /*
      写如文对象到件 -object
   */
  @Test
  def test3(): Unit = {
    val list = List("hello", "hi", "spark")
    val rdd1 = sc.makeRDD(list, 1)
    rdd1.saveAsObjectFile("object-file")

  }

  /*
    从文件中读 文本
   */
  @Test
  def test2(): Unit = {
    val rdd1 = sc.textFile("txt-output")
    rdd1.foreach(println)
  }

  /*
      文本写入文件-txt
   */
  @Test
  def test1(): Unit = {
    val list = List(1, 2, 3, 4)
    val rdd1 = sc.makeRDD(list, 2)

    rdd1.saveAsTextFile("txt-output")
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