package com.asher.function

object VariableParam {
  def main(args: Array[String]): Unit = {
    def sum(n: Int*) = {
      var res = 0
      n.foreach(res += _)
      res
    }

    println(sum(1))
    println(sum(1, 2, 3))

    val arr = Array(1,2,3,4)
    //sum(arr)
    sum(arr(0),arr(1),arr(2))
    println(sum(arr: _*))
  }

}
