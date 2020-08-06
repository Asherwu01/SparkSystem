package com.asher.spark.core_project.base

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * 创建环境，释放环境，输出目录
 */
abstract class BaseApp {

  //输出目录
  var outputPath: String
  val sc = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("App"))

  // 控制抽象，传入需要运行的代码
  def runApp(op: => Unit) = {
    //初始化
    init()

    try {
      //业务逻辑
      op
    } catch {
      case e: Exception => println(e.getMessage)
    } finally {
      //释放资源
      stop()
    }
  }


  // 创建环境
  def init() = {
    val fs = FileSystem.get(new Configuration())
    val path: Path = new Path(outputPath)
    if (fs.exists(path)) {
      fs.delete(path, true)
    }
  }

  def stop(): Unit = {
    sc.stop()
  }
}


