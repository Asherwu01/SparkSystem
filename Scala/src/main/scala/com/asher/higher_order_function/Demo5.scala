package com.asher.higher_order_function

object Demo5 {
  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3, 4, 5)
    val res = list.foldRight(0)(_ + _)
    println(res)
  }
}
