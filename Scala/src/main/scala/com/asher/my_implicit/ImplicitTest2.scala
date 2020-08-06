package com.asher.my_implicit

import java.io.File

import scala.io.Source

object ImplicitTest2 {
  def main(args: Array[String]): Unit = {
    //提供隐士转换函数，将File转成RichFile
    implicit def fileToRichFile(file: File) = new RichFile(file)

    val file = new File("F:\\Develop\\IdeaProject\\SparkSystem\\Scala\\src\\main\\scala\\com\\asher\\my_implicit\\ImplicitTest.scala")

    println(file.readContent)
  }
}

class RichFile(file: File) {

  def readContent(): String = {
    Source.fromFile(file,"utf-8").mkString
  }
}