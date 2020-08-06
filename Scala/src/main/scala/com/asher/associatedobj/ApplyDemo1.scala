package com.asher.associatedobj

object ApplyDemo1 {
  def main(args: Array[String]): Unit = {
    //调用对象
    A()
    A.apply()
    val a = new A
    // a 不写括号，相当于写了一个对象的地址，啥都没干
    a()

    a.apply()

    (foo _).apply()
    }
    def foo() = {
      println("foo... ")
  }
}

object A{
  def apply()= println("Object A ....apply")
}

class A{
  def apply() = println("class A .....apply")
}
