package com.asher.higher_order_function

object HigherOrderFunTest {
  def main(args: Array[String]): Unit = {
    //map 转换为 list
    val map = Map("a" -> 1, "b" -> 2, "c" -> 3)
    //map.map(kv => kv._1)
    val iterable = map.map(_._1)
    println(iterable)

    val list = List(1, 23, 43, 2, 4)
    val list1 = list.filter(x => x > 4)
    println(list1 eq(list))
    println(list)
  }
}
