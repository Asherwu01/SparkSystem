package com.asher.exercise

object ScanTest {
  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3, 4)
    val res = list.scanLeft(0)((x, y) => x + y)
    println(res)
  }
}
