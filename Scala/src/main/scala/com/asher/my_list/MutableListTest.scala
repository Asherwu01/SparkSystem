package com.asher.my_list

import scala.collection.mutable.ListBuffer

object MutableListTest {
  def main(args: Array[String]): Unit = {
    val buffer = ListBuffer(10,20,30)
    val buffer1 = new ListBuffer[Int]()

    buffer += 40
    100 +=: buffer
    println(buffer.mkString(" "))

    buffer1 += 10
    println(buffer1.mkString(" "))

  }
}
