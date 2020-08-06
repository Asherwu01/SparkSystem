package com.asher.function

object MathodToFun {
  def main(args: Array[String]): Unit = {
    val function = add1 _
    println(function(1, 2))
    println(new Cacl().add("hello"))
    println(new CaclSon().add("world"))
    println(new CaclSon().add(2, 4))

  }
  def add1(a: Int,b:Int): Int ={
    a+b
  }
}

class Cacl{
  def add(a: Int,b:Int): Int ={
    a+b
  }

  def add(str: String): String ={
    str
  }

}

class CaclSon extends Cacl{
  override def add(a: Int, b: Int): Int = a*b
}