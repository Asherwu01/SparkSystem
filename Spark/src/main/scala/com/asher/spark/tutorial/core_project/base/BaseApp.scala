package com.asher.spark.tutorial.core_project.base

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by VULCAN on 2020/7/17
 *
 *    完成  创建环境，释放环境，删除输出目录
 */
abstract class BaseApp {

  //应用输出的目录
  val outPutPath : String

  val sc = new SparkContext(new SparkConf()

    .setAppName("My app")
    .setMaster("local[*]")
  )

  //核心： 运行APP
  def runApp( op: =>Unit ) ={

    //清理输出目录
    init()

    //核心运行子类提供的代码
    try {
      op
    } catch {
      case e:Exception => println("出现了异常:"+e.getMessage)
    } finally {
      //关闭连接
      sc.stop()
    }

  }


  def init(): Unit ={

    val fileSystem: FileSystem = FileSystem.get(new Configuration())

    val path = new Path(outPutPath)

    // 如果输出目录存在，就删除
    if (fileSystem.exists(path)){
      fileSystem.delete(path,true)
    }

  }


}
