package com.asher.function

object ClosePackage {
  def main(args: Array[String]): Unit = {
    val ff1 = fun1()
    println(ff1()) //2

    val ff2 = fun2()
    println(ff2()) //2
    println(ff2()) //3
    var ff3 = fun2()
    println(ff3()) //2
    println(ff3()) //3
  }

  def fun1(): () => Int = {
    var a = 1
    val f = () => {
      println("f调用了")
      a
    }
    a += 1
    f
  }

  def fun2(): () => Int = {
    var a = 1
    () => {
      a += 1
      a
    }
  }
}
