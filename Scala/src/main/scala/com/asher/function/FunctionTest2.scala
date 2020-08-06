package com.asher.function

object FunctionTest2 {
  def main(args: Array[String]): Unit = {

    highterOrderFunction1(str => str+"！！！")

    val function = highterOrderFunction2("hello")
    println(function("world"))

    val f = highterOrderFunction3(i => i + 1)
    val str = f("haha")
    println(str)

    val str1 = highterOrderFunction3("你")("好")
    println(str1)
  }


  //高阶函数
  //参数为函数
  def highterOrderFunction1(f:String=>String): Unit ={
    val str = f("高阶函数")
    println(str)
  }


  //返回值为函数
  def highterOrderFunction2(s: String): String => String ={
    (str: String)=>{
      str + "，说完了" + s
    }
  }

  //参数和返回值都为函数
  def highterOrderFunction3(s: Int=>Int): String => String ={
    s => s+"hello"
  }

  //函数柯里化
  def highterOrderFunction3(s: String)(s1: String) ={
    s + "，说完了" + s1
  }



}
