package com.asher.my_pattern

object PatternMatch2 {
  def main(args: Array[String]): Unit = {
    val a:Any = 100
    //val a:Any = true

    a match {  //case中匹配项的变量都是以当前case作为作用域的局部变量
      case b:Int if b>90 && b<100 =>
        println(s"b= $b")
      case 100 =>
        println(s"a= $a")
      case b:Boolean =>
        println("我是boolean值")
      case _ =>
        println("结束啦。。。")
    }
  }
}
