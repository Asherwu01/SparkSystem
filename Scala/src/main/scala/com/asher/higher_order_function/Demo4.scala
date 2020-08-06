package com.asher.higher_order_function

object Demo4 {
  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3, 4, 5)
    val res = list.reduce((x, y) => x + y)
    println(res)

    val list1 = List("a", "b", "c", "d")
    val str = list1.reduce((x, y) => x + "->" + y)
    println(str)

  }
}
