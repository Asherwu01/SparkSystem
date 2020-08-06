package com.asher.exercise

object Demo2 {
  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3, 4, "abc", null, false) //计算所有偶数的和
    val list2 = list.filter(x => {
      if (x.isInstanceOf[Int]) true
      else false
    })
    val list1 = list2.map(x => x.asInstanceOf[Int])
    val sum = list1.filter(_ % 2 == 0).sum
    println(sum)




  }
}
