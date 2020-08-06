package com.asher.exeception

object ExeceptionTest {
  def main(args: Array[String]): Unit = {
    val a = 4
    val b = 0

    val value = try {
      a / b
    } catch {
      case e: Exception => println(e.getMessage); 10
    } finally {
      println("执行")
    }
    println(value)
    show()
  }

  @throws[IllegalArgumentException]
  def show(): Unit ={
    //println(10 / 0)
    throw new RuntimeException()
  }
}

class Point[T](x:T,y:T){  //泛型类
  def show(r:T): Unit ={
    println(r)
  }

  def move[K](x:K,y:K): Unit ={//定义的泛型方法
    val z:K = x
    println(x+"=="+y+"==="+z)
  }
}