package com.asher.array

object ImmutableArrayTest {
  def main(args: Array[String]): Unit = {
    val arr1 = Array(1, 2, 3, 4)
    arr1.foreach(println)

    val arr2 = new Array[Int](4)
    arr2(0)=1
    arr2(1)=2
    arr2(2)=3
    arr2(3)=4
    arr2.foreach(println)

    val arr3 = arr2 :+ 100
    val arr4 = 100 +: arr3

    val arr5 = Array("aa", "bb")

    println(arr4.mkString(","))
    val arr6 = arr3 ++ arr5
    println(arr6.mkString(","))

  }

}
