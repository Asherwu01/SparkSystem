package com.asher.exercise

object Demo3 {
  def main(args: Array[String]): Unit = {
    //合并2个map
    val map1 = Map("a" -> 97, "b" -> 98)
    val map2 = Map("b" -> 980, "c" -> 99)

    val res = map1.foldLeft(map2)((map, kv) => {
      val value = map2.getOrElse(kv._1, 0) + kv._2
      map + (kv._1 -> value)
    })
    println(res)


  }
}
