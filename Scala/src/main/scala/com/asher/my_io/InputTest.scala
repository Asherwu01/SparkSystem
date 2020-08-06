package com.asher.my_io

import scala.io._

object InputTest {
  def main(args: Array[String]): Unit = {
    //val source = Source.fromFile("F:\\Develop\\IdeaProject\\SparkSystem\\Scala\\input\\word.txt")
    val source = Source.fromFile("Scala/input/word.txt")
    val iterator = source.getLines()
    while (iterator.hasNext) {
      println(iterator.next())
    }
    source.close()
  }
}
