package com.asher.my_tuple

object TupleTest2 {
  def main(args: Array[String]): Unit = {
    val tuple = /%(4, 3)
    println(tuple._1)
    println(tuple._2)

    //val (a, b):(Int,Int) = /%(5, 7)
    //println(a)
    //println(b)

    val (a, _):(Int,Int) = /%(5, 7)
    println(a)

  }

  def /%(m: Int, n: Int): (Int, Int) = {
    (m / n, m % n)
  }
}
