package com.asher.my_map

object MapTest3 {
  def main(args: Array[String]): Unit = {
    // 创建不可变map
    val map = Map("id" -> "1", "name" -> "tome", "gender" -> "male")

    val str = map("name")
    println(str)

    val str1 = map.getOrElse("sex", "false")
    println(str1)

  }
}
