package com.asher.exercise

object SumTest {
  def main(args: Array[String]): Unit = {
    println(sum1(1, 2, 3))
    val f1 = sum2(1)
    val f2 = f1(2)
    val res = f2(3)
    println(res)

    println(sum3(1)(2)(3))
  }

  def sum1(a:Int,b:Int,c:Int)={
    a+b+c
  }

  def sum2(a:Int): Int=>Int=>Int ={
    def add(b:Int)={
      def add2(c:Int)={
        a+b+c
      }
      add2 _
    }
    add _
  }

  def sum3(a:Int)(b:Int)(c:Int):Int={
    a+b+c
  }
}
