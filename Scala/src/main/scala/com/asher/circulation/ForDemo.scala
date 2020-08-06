package com.asher.circulation

import scala.util.control.Breaks._

object ForDemo {
  def main(args: Array[String]): Unit = {
    // 判断质数
    val n = 9
    var isPrime = true

    /*try{
      for(i <- 2 until n){
        if(n % i == 0){
          isPrime = false
          throw new RuntimeException
        }
      }
    }catch{
      case e =>
    }
    if(isPrime){
      println(s"$n 是质数")
    }else{
      println(s"$n 是合数")
    }*/

    // 函数式编程，简化
    /*Breaks.breakable{
      for (i <- 2 until n) {
        if (n % i == 0) {
          isPrime = false
          Breaks.break()
        }
      }
    }*/

    breakable{// 内部是一个方法，捕捉了抛出的异常
      for (i <- 2 until n) {
        if (n % i == 0) {
          isPrime = false
          break  //内部是一个方法，在抛异常，scala没有break关键字
        }
      }
    }

    if (isPrime) {
      println(s"$n 是质数")
    } else {
      println(s"$n 是合数")
    }

    for(i <- 1 to 9) yield {
      i*i
      print(s"${i*i}  ")
      if(i == 9) println
    }
  }
}
