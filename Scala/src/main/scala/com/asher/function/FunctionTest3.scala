package com.asher.function

object FunctionTest3 {
  def main(args: Array[String]): Unit = {
    val array = Array(12, 3, 23, 5)
    println(reduce(array, _ + _))

  }

  def reduce(arr: Array[Int], f: (Int, Int) => Int): Int = {
    var res:Int = arr(0)
    for (i <- 1 until arr.length) {
      res=f(res,arr(i))
    }
    res
  }
}
