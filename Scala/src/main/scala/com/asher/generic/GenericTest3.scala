package com.asher.generic


object GenericTest3 {
  def main(args: Array[String]): Unit = {
    //val fs:MyList[Human] = new MyList[Person]  //class MyList[T] 不变

    //val fs:MyList[Human] = new MyList[Person]  //class MyList[+T] 协变

    val fs:MyList[Person] = new MyList[Human]  //class MyList[-T] 逆变
  }
}

class MyList[-T]

class Human

class Person extends Human
