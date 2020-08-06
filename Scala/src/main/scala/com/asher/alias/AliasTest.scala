package com.asher.alias

object AliasTest {
  def main(args: Array[String]): Unit = {
    type T = com.asher.alias.ADAFASDFSDAV //给类取别名
    val t = new T
    t.show()
  }
}

class ADAFASDFSDAV {
  def show()={
    println("ADAFASDFSDAV")
  }
}