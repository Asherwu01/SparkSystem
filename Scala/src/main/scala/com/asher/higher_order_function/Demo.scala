package com.asher.higher_order_function

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

object Demo {
  def main(args: Array[String]): Unit = {
    val list = List(20, 30, "ad", false, "dfas")
    val list1 = list.filter(elem => elem.isInstanceOf[Int])
    list1.map(e => {
      if (e.isInstanceOf[Int]) e.asInstanceOf[Int]
    })
    println(list1)
    val list2 = list.filter(_.isInstanceOf[Int]).map(_.asInstanceOf[Int])
    println(list2)

    val list3 = List(Array(10, 20), Array(1, 2), Array(5, 6, 7))
    val flatten = list3.flatten
    println(flatten)

    val list4 = List("hello word", "hello hello", "hello asher")
    val flatten1 = list4.map(x => x.split(" ")).flatten
    println(flatten1)

    val list6 = list4.flatMap(_.split(" "))
    println(list6)
  }
}
