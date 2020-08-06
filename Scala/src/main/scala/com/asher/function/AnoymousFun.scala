package com.asher.function

object AnoymousFun {
  def main(args: Array[String]): Unit = {
    cacl(10,20,(a: Int,b: Int)=>a+b)//匿名函数
    //cacl(10,20,(a: Int,b: Int)=>a+b)//匿名函数
  }

  def cacl(a: Int,b: Int,op:(Int,Int)=>Int) = op(a,b)
}
