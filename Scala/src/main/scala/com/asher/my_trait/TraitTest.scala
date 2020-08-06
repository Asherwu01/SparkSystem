package com.asher.my_trait

object TraitTest extends{
  def main(args: Array[String]): Unit = {
    val c = new C
    c.printB()

  }
}



class A{
  def eat() ={
    println("A eat...")
  }
}

trait B {
  // s是一个变量名，可以用 _,对应调用要用this
  //s: A =>
  //s.eat()
  _ : A =>
  this.eat()   // eat()
  def printB(): Unit ={
    this.eat()
  }
}

class C extends A with B


/*
class M extends A{}
trait B extends A{//特质继承类

}

class C extends M with B{

}*/
