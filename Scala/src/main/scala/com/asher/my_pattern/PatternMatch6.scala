package com.asher.my_pattern

object PatternMatch6 {
  def main(args: Array[String]): Unit = {
    val list = List(10, 20, 30)
    list match {
      case List(a,b,c) =>
        println(a)
        println(b)
        println(c)

     /* case List(a,rest@_*) =>
        println(rest)*/

      /*case head :: rest =>
        println(head)
        println(rest)*/

      /*case one::two::three::Nil =>
        println(three)*/

      /*case one::two::three =>
        println(three)*/
    }
  }
}
