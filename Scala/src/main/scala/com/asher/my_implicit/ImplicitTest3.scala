package com.asher.my_implicit

import java.io.File

import scala.io.Source

object ImplicitTest3 {
  def main(args: Array[String]): Unit = {

    val file = new File("F:\\Develop\\IdeaProject\\SparkSystem\\Scala\\src\\main\\scala\\com\\asher\\my_implicit\\ImplicitTest.scala")
    println(file.readContent)
  }

  //注意隐式类定义的作用域
  implicit class RichFile(file: File) {
    def readContent(): String = {
      Source.fromFile(file, "utf-8").mkString
    }
  }
}

