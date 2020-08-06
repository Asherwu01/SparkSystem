package com.asher.exercise

object MapTest {
  def main(args: Array[String]): Unit = {
    val arr = Array(1, 2, 3, 4)
    println(map(arr,_ * 2).mkString(","))
    //val function:(Int,String)=>String = fun
    val function = fun _
    println(function.isInstanceOf[Function1[Int, Int]])

    val max = (x: Int, y: Int) => if (x < y) y else x

    val anonfun2 = new Function2[Int, Int, Int] {
      def apply(x: Int, y: Int): Int = if (x < y) y else x
    }
    assert(max(0, 1) == anonfun2(0, 1))

  }

  def fun(a:Int,b:String): String ={
    a+b
  }

  def map(arr:Array[Int],op:Int => Int):Array[Int]={
    for (elem <- arr) yield op(elem)
  }
}
