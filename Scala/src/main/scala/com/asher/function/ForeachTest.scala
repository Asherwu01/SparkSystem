package com.asher.function

  object ForeachTest {
    def main(args: Array[String]): Unit = {
      //foreach(Array(1,2,3),a=>println(a))
      foreach(Array(1,2,3),println)
      Array(1,3,5).foreach(println)
      val arr = Array(1,2,3,4,5)
      val ints = filter(arr, (a) => a % 2 == 0)
      ints.foreach(println)
      println("================")
      val ints1 = arr.filter(_ % 2 == 0)
      ints1.foreach(println)
      val a = null
      val b: Any = a
      println(b)
      println("================")
      val ints2 = map(arr, a => a * a)
      foreach(ints2,println)
      println("================")
      val ints3 = arr.map(a => a * a)
      foreach(ints3,println)
      println("=====================")
      val arr1 = Array(1, 4, -3, 9, 36)
      filter(arr1,_>0).map(a=>math.sqrt(a)).foreach(println)

      println(arr.mkString(","))

    }

    def foreach(arr:Array[Int],opt:Int=>Unit): Unit ={
      for (elem <- arr) {
        opt(elem)
      }
    }

    def filter(arr:Array[Int],condition:Int=>Boolean) ={
      for (elem <- arr if(condition(elem)) ) yield elem
    }

    def map(arr:Array[Int],opt:Int=>Int) = for (elem <- arr) yield opt(elem)
  }
