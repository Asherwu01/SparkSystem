package com.asher.my_pattern

object PatternMatch10 {
  def main(args: Array[String]): Unit = {
    val List(a,b,c,d) = List(1, 2, 3, 4)
    println(b)

    val map = Map("a" -> 97, "b" -> 98, "c" -> 99)
    for ((k,v)<- map){
      println(k)
    }
  }
}
