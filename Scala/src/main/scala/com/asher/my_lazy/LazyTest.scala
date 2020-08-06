package com.asher.my_lazy

object LazyTest {
  def main(args: Array[String]): Unit = {
    lazy val res = sum(1,2)
    println(res)
    var temp =f _
    println(f())
    println(temp)
    println((x:Int)=>x+1)
  }

  def sum(a: Int, b: Int) = {
    println("sum。。。。")
    a + b
  }
  def f(){}
}
