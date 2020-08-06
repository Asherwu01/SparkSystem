package com.asher.my_exetends

object A {
  def main(args: Array[String]): Unit = {
    //val c = new C
    //c.cPrint()

    val b = new B
    println(b.name)


  }
}


private class B{
  protected val age: Int = 10
  val name: String = "BB name"
}


/*
class C extends B {
  protected val name: Int = 1
  var id:Int = 1

  def cPrint()={
    println(age)
  }
}*/
