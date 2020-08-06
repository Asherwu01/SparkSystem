package com.asher.function

import scala.util.control.Breaks._

object Factorial {
  def main(args: Array[String]): Unit = {
    var n =1
    while (n<100) {
      val res = factorialFun(n)
      println("阶乘："+res)
      if (res == 0) {
        println(n)

      }
      n += 1
    }

  }

  def factorialFun(num: Int): Int = {
    if (num == 1) 1
    else num * factorialFun(num - 1)
  }
}
