package com.asher.my_exetends

object OverrideTest {
  def main(args: Array[String]): Unit = {
    val son = new Son
    println(son.age)
  }
}
class Father{
  val age: Int = 1
}

class Son extends Father {
  override val age: Int = 2
  def show(): Unit ={
    //println(super.age)
  }
}