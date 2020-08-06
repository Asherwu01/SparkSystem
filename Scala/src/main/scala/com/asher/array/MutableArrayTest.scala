package com.asher.array

import scala.collection.mutable.ArrayBuffer

object MutableArrayTest {
  def main(args: Array[String]): Unit = {
    //方式一
    val buffer = ArrayBuffer(1, 2, 3, 4, 5.5)

    //方式二
    val buffer1 = new ArrayBuffer[Int]()
    println(buffer1.toString)

    val buffer2 = 100 +: buffer :+ 100
    println(buffer.mkString(","))
    println(buffer1.mkString(","))
    println(buffer2.mkString(","))

    val buffer4 = buffer1 += 100
    val buffer5 = 200 +=: buffer1
    println(buffer1.mkString(", "))
  }

}
