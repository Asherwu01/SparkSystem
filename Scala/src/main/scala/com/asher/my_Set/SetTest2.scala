package com.asher.my_Set

import scala.collection.mutable

/*
1. 创建可变集合mutable.Set
2. 打印集合
3. 集合添加元素
4. 向集合中添加元素，返回一个新的Set
5. 删除数据
 */
object SetTest2 {
  def main(args: Array[String]): Unit = {
    val sets = mutable.Set(1, 2, 4, 6, 3, 2, 1)
    sets.foreach(println)

    val s = sets += 100
    println(sets.mkString(" "))
    println(s.mkString(" "))

    sets -=100
    println(sets.mkString(" "))
  }
}
