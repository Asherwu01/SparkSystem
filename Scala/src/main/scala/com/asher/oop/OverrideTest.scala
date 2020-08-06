package com.asher.oop

object OverrideTest {
  def main(args: Array[String]): Unit = {
    val b = new B
    b.say
  }

}

abstract class A {
  var name:String

  def say()={
    println("A....")
  }
}
class B extends A{
  override var name = "bbb"
  override def say = {
    var i = 10
    println(i)
  }
}

