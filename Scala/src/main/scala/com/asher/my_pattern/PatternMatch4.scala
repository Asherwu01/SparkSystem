package com.asher.my_pattern

object PatternMatch4 {
  def main(args: Array[String]): Unit = {
    val arr: Any = Array(1, 2, 3, 4)

    arr match {
      /*case Array(a,b,c) =>
        println(a)*/

      /*case Array(1,b,c) =>
        println(b)*/

      /*case Array(1, a, _, 4) =>
        println(a)*/

      /*case Array(a,_*) =>
        println(a)*/

      case Array(a,rest@_*) =>
        println(rest)
    }
  }
}
