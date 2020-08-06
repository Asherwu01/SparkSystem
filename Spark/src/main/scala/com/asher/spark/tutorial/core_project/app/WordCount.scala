package com.asher.spark.tutorial.core_project.app

import com.asher.spark.tutorial.core_project.base.BaseApp

/**
 * Created by VULCAN on 2020/7/17
 */
object WordCount extends  BaseApp{

  override val outPutPath: String = "output/wordcount"

  def main(args: Array[String]): Unit = {

    runApp{

      sc.makeRDD(List(1,2,3,4),2).saveAsTextFile(outPutPath)


    }
  }

}
