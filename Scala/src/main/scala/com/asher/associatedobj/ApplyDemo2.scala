package com.asher.associatedobj

object ApplyDemo2 {
  def main(args: Array[String]): Unit = {
    val arr = new MyArray(12, 424, 13)
    println(arr(0))
    println(arr(1))
  }
}

class MyArray(args:Int*){
  def apply(index: Int) = args(index)
}