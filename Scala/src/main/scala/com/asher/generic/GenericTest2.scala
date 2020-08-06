package com.asher.generic

object GenericTest2 {
  def show[T >: Father](t: T) = {println("执行")} //下限，推导时和上限不一样，子类也可以

  def main(args: Array[String]): Unit = {
    show(new Father)
    show(new Grandpa)
    show(new Son)
  }
}

class Grandpa {}

class Father extends Grandpa {}

class Son extends Father {}