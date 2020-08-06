package com.asher.my_pattern
case object Check
object PatternMatch9 {
  def main(args: Array[String]): Unit = {

    Check match{

      case Check => println(Check)

    }
  }
}
