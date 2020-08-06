package com.asher.function

object FunctionTest4 {
  def main(args: Array[String]): Unit = {
   // println(foo(f))
    foo(3+4)
  }

  def f()={
    println("f被调用了")
    10
  }

  def foo(a: =>Int)={//注意，此处是函数省略，又称为名调用，等号和：之间必须要有空格 ()=>为函数省略()的结果
    a
    a
    a
  }
}
