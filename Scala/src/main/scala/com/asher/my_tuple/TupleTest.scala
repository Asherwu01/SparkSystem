package com.asher.my_tuple

object TupleTest {
  def main(args: Array[String]): Unit = {
    // 定义元组
    val tuple = Tuple2(1, "tom")
    val t2 = (2,"jack")
    println(tuple._1)
    println(tuple._2)

    val iterator = t2.productIterator
    for (elem <- iterator) {
      println(elem)
    }
  }
}
