package com.asher

object DefualtValueFun {
  def main(args: Array[String]): Unit = {
    println(add(10, 20))
    println(add(10))
  }

  def add(a: Int, b: Int = 10) = {
    a + b
  }
}
