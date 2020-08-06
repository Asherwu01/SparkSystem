package com.asher.my_pattern

object PatternMatch1 {
  def main(args: Array[String]): Unit = {
    val BB = 10
    val res = BB match {
      case BB =>
        println(BB+"a") //10a
        100
      case 30 =>
        println("30")
      case _ =>
        println("匹配失败")

    }
    println(res) //100

    val a =110
    a match {
      case aBfd =>
        println(aBfd+"a") //110a
      case 30 =>
        println("30")
      case _ =>
        println("匹配失败")
    }
  }
}
