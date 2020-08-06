package com.asher.my_implicit

object ImplicitTest4 {
  // 定义隐式参数
  implicit val num:Int = 10
  def main(args: Array[String]): Unit = {
    show(10)
    show
  }

  //隐式参数和隐式值都是配套使用
  def show(implicit a:Int)={
    println(a)
  }
}
