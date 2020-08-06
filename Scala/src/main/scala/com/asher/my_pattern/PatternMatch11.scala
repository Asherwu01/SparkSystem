package com.asher.my_pattern

object PatternMatch11 {
  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3, false, "hello")
    //需求：只对list中整数 乘2，然后将结果加入新集合返回

    // 偏函数
    /*var f = new PartialFunction[Any, Int] {
      //如果函数返回true，则会对x进行处理
      //过滤出Int
      override def isDefinedAt(x: Any): Boolean = x match {
        case _: Int => true
        case _ => false
      }

      override def apply(v1: Any): Int = v1 match {
        case a: Int => a * 2
      }
    }
    val res = list.collect(f)
    println(res)*/

    val res = list.collect({
      case a: Int => a * 2
    })
    println(res)
  }
}
