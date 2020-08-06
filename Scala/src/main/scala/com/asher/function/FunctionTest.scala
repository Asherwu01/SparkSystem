package com.asher.function

object FunctionTest {
  def main(args: Array[String]): Unit = {
    //方法嵌套定义
    def say(str: String) = {
      println(str)
    }
    val a = 10
    say("hello")
    main()

    variableParameter("aa", "bb")

    println(factorial(5))
    fun("ada")
    f1()

    highterOrderFunction1(str => str+"！！！")


    //val function = highterOrderFunction2("hello")
    //println(function("world"))
  }

  //方法定义
  def main(): Unit = {
    println("overload...")
  }

  //可变参数的函数
  def variableParameter(args: String*): Unit = {
    args.foreach(println)
  }

  //递归函数
  def factorial(num: Int): Int = {
    if (num == 1) 1 else num * factorial(num - 1)
  }

  //匿名函数
  val fun= (str: String)=>{
    println(str)
  }
  val f1= ()=>{
    println("adfsadf")
  }

  //高阶函数
  //参数为函数
  def highterOrderFunction1(f:String=>String): Unit ={
    val str = f("高阶函数")
    println(str)
  }

  //返回值为函数

  //参数和返回值都为函数

  //

}
