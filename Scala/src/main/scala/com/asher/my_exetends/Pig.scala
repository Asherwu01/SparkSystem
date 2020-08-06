package com.asher.my_exetends

object Pig extends Animal {
  def main(args: Array[String]): Unit = {
    val animal = new Animal
    val cat = new Cat
    super.say()
    //cat.printAge()
    //super.say()
  }
}

class Cat extends Animal {
    def printAge(): Unit ={
      println(age1)
    }
}

class Animal {
  protected val age1 = 10
  val name:String = "animal"
  protected def say()={
    println("哇哇。。。。")
  }
}