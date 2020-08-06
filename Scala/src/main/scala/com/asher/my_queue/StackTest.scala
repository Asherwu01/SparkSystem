package com.asher.my_queue

import scala.collection.mutable

object StackTest {
  def main(args: Array[String]): Unit = {
    val stack = mutable.Stack(1, 2, 3)
    stack.push(4)
    println(stack)
    stack.pop()
    println(stack)

    val range = (1 to 10).par
    range.foreach(x=>{
      println(x+"  "+Thread.currentThread().getName)
    })

  }
}
