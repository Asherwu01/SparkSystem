package com.asher.exercise

object HigeMethod {
  def main(args: Array[String]): Unit = {
    //过滤正数，计算平方根，遍历打印
    val list = List(1, 4, -3, 81)
    val list1 = list.filter(_ > 0)
    val list2 = list1.map(a => Math.sqrt(a))
    list2.foreach(println)
  }

}
