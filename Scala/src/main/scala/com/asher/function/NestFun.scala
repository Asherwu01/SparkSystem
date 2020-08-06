package com.asher.function

object NestFun {
  def main(args: Array[String]): Unit = {
    println(addOps(1, 2))
    println(sum(b = 1))
  }

  def addOps(a: Int,b: Int): Int ={
    def max(m: Int,n: Int):Int={
      if (m>n) m else n
    }
    val res: Int = max(a,b)
    a + res*res
  }

  def sum(a:Int = 1,b:Int): Int = a+b
}
