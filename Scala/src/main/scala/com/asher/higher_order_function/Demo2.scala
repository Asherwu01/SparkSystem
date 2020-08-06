package com.asher.higher_order_function

object Demo2 {
  def main(args: Array[String]): Unit = {
    /*
        1.返回新集合
        2.如果是奇数，只包含3次方
        3.如果是偶数，包含2次方和3次方
     */
    val list = List(1, 2, 3, 4, 5)
    val list1 = list.flatMap(x => {
      if (x % 2 == 0) Array(x, x * x, x * x * x)
      else Array(x, x * x)
    })
    println(list1)
  }
}
