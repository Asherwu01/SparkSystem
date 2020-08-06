package com.asher.my_Set

object SetTest3 {
  def main(args: Array[String]): Unit = {
    // set集合的数学运算
    val set1 = Set(1, 20, 30, 10, 8)
    val set2 = Set(5, 30, 20, 60, 3)
    //并集
    println(set1 | set2)
    println(set1 ++ set2)
    println(set1.union(set2))


    //交集
    println(set1 & set2)
    println(set1.intersect(set2))

    //差集
    println(set1 &~ set2)
    println(set1 -- set2)
    println(set1.diff(set2))

  }
}
