package com.asher.my_enumeration

object EnumerationTest2 {
  def main(args: Array[String]): Unit = {
    println(WeekDay.Mon)
  }
}

object WeekDay extends Enumeration {
  type WeekDay = Value
  val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
}