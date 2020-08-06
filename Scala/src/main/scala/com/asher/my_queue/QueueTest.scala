package com.asher.my_queue

import scala.collection.immutable.Queue
import scala.collection.mutable

object QueueTest {
  def main(args: Array[String]): Unit = {
    //不可变queue
    var queue = Queue(1, 2)
    val queue1 = queue.enqueue(3)
    val tuple = queue.dequeue
    //println(tuple._1)
    //println(tuple._2)

    //可变queue
    val queue2 = mutable.Queue(1, 2)
    queue2.enqueue(3)
    println(queue2)
    queue2.dequeue()
    println(queue2)

  }
}
