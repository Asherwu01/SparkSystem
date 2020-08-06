package com.asher.function

class FunctionTest5 {
  def say(): Unit = {
    println("say...")
  }

  val f: () => Unit = () => println("hela")
}

object MyTest {
  def main(args: Array[String]): Unit = {
    val functionTest = new FunctionTest5
    functionTest.say()
  }

  def say1(): Unit = {
    print("say1...")
  }

  var say11: () => Unit = say1

  var say3 = () => {
    println("say2....")
  }
}