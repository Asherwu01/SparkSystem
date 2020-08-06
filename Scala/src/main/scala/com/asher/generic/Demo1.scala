package com.asher.generic

object Demo1 {
  def main(args: Array[String]): Unit = {
    println(!10)
  }

  //定义一个运算符  !2 = 10 -2  !4=10-4
  implicit class RichInt(n: Int) {
    def unary_! : Int = 10 -n
  }

}
