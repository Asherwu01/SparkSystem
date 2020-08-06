package com.asher.currying

import java.util.concurrent.locks.Condition

object DefineLoop {
  def main(args: Array[String]): Unit = {

    //打印1-100，即循环100次
    var i = 0
    /*while (i<100){
      println(i)
      i+=1
    }*/

    //调用my_while
    myWhile(i < 100) {
      println(i)
      i += 1
    }

    println("=========================")
    val array = Array(1, 2, 32, 23, 12)
    var j =0
    myWhile(j < array.length) {
      println(array(j))
      j += 1
    }
  }


  //自定义while
  def myWhile(condition: => Boolean)(op: => Unit): Unit = {
    if (condition) {
      op
      myWhile(condition: Boolean)(op)
    }
  }
}
