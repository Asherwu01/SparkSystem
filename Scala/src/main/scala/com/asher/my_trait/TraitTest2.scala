package com.asher.my_trait

object TraitTest2 {
  def main(args: Array[String]): Unit = {
    var a1:A1 = new B1
    a1.foo()

    if (a1.isInstanceOf[B1]) {
      val b1 = a1.asInstanceOf[B1]
      b1.printB1()
    }
  }
}

class A1 {

  def foo(): Unit ={
    println("A foo...")
  }
}

class B1 extends A1 {
  override def foo(): Unit = println("B foo ...")
  def printB1(): Unit ={
    println("B1 特有方法 ...")
  }
}