package com.asher.my_collections

import scala.collection.mutable

object SetTest {
  def main(args: Array[String]): Unit = {
    import scala.collection.mutable.Set
    val set = Set[Int](1,2,3)
    val ints: mutable.Set[Int] = set.+(100)

    ints.foreach(i=>println(i))
    set.foreach(i=>println(i))
    val value: set.type = set.+=(20)
    set.foreach(println)
    println("=====")
    set.foreach(println)
  }

}
