package com.asher.my_map

import scala.collection.mutable

object MapTest4 {
  def main(args: Array[String]): Unit = {
    val map = mutable.Map("a" -> 1, ("b", 3))
    map += (("c", 4))
    map.foreach(println)
    map.getOrElseUpdate("a",3)
  }
}
