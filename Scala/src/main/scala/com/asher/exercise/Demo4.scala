package com.asher.exercise

object Demo4 {
  def main(args: Array[String]): Unit = {
    val map = Map("河北" -> ("衣服", 6), "河南" -> ("电脑", 2))
    val map1 = map.mapValues(kv => kv._2)
    println(map1)

    (0 to 100).par.foreach(x=>print(x+" "))
    println("=================")
    (0 to 100).foreach(x=>print(x+" "))
  }
}
