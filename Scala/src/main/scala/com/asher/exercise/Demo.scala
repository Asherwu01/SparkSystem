package com.asher.exercise

import scala.collection.mutable.ListBuffer

object Demo {
  def main(args: Array[String]): Unit = {
    val list = List(ListBuffer(1, 2, 3), ListBuffer(4, 5, 6))
    var buffer:List[Int] = list.flatMap((buffer:ListBuffer[Int] )=> {
      buffer += 100
    })
    println(buffer)
  }

}
