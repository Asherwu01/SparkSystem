package com.asher.frequence_method

object MethodTest2 {
  def main(args: Array[String]): Unit = {
    val list1 = List(1, 2, 3, 4, 7, 6, 5)
    val list2 = List(2, 3, 4, 9, 6, 5)
    //（1）获取集合的头
    println(list1.head)

    //（2）获取集合的尾（不是头的就是尾）
    println(list1.tail)

    //（3）集合最后一个数据
    println(list1.last)

    //（4）集合初始数据（不包含最后一个）
    println(list1.init)

    //（5）反转
    println(list1.reverse)

    //（6）取前（后）n个元素
    println(list1.take(3))
    println(list1.takeRight(3))

    //（7）去掉前（后）n个元素
    println(list1.drop(3))
    println(list1.dropRight(3))

    //（8）并集
    println(list1.union(list2))

    //（9）交集
    println(list1.intersect(list2))

    //（10）差集
    println(list1.diff(list2))

    //（11）拉链 注:如果两个集合的元素个数不相等，那么会将同等数量的数据进行拉链，多余的数据省略不用
    println(list1.zip(list2))
    // list1.zipAll(list2,list1的默认值，list2的默认值)
    println(list1.zipAll(list2, -1, -2))
    // list1.zipWithIndex //元素和自己的索引进行拉链
    println(list1.zipWithIndex)

    val list3 = List(1, 2, 3, 4, 7, 6, 5)
    //（12）滑窗
    val iterator = list3.sliding(2, 5)//窗口刚好到最后，就不会再往下走
    println(iterator.mkString(" "))
  }
}
