package com.asher.my_implicit

object ImplicitTest {
  def main(args: Array[String]): Unit = {
    //定义隐士转换函数
    implicit def doubleToInt(d: Double) = d.toInt

    //召唤系统定义好的隐式值
    val order = implicitly[Ordering[Int]]
    var i: Int = 10.1
    //var i: Int = 10.1.toInt
    println(i)
  }
}
