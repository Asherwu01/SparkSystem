package com.asher.my_pattern

object PatternMatch3 {
  def main(args: Array[String]): Unit = {
    val arr:Any=Array(1,2,3)

    arr match {
      case a:Array[Int] =>
        println("Array[Int]")
      case a:List[Int] =>
        println("List[Int]")
    }
  }
}
