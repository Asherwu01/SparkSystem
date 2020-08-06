package com.asher.oop

object ImportTest {
  def main(args: Array[String]): Unit = {
    val d = new Dog()
    d.eat()

    //导入对象的成员
    import d._
    eat()
  }
}

class Dog {
  def eat(): Unit = {
    println("eat...")
  }
}
