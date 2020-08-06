package com.asher.my_list

object ImmutableListTest {
  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3, 4)

    val list1 = list :+ 10
    val list2 = 10 +: list
    println(list.mkString(" "))
    println(list1.mkString(" "))
    println(list2.mkString(" "))

    val list4 = list1 ++ list2
    val list5 = list1 ::: list2
    println(list4.mkString(" "))
    println(list5.mkString(" "))

    //list 专属在头部添加元素的方法
    val list3 = 100 :: list
    println(list.mkString(" "))
    println(list3.mkString(" "))

    //定义空集合，两种方式
    val list6 = List[Int]()
    Nil

    val list7 = 1 :: 2 :: 3 :: 4 :: Nil
    println(list7.mkString(" "))

  }
}
