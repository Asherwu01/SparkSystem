package com.asher.my_inner

object InnerClassTest2 {
  def main(args: Array[String]): Unit = {
    val outer1 = new Outer
    val inner1 = new outer1.Inner

    val outer2 = new Outer
    val inner2 = new outer2.Inner

    inner1.show(inner1)
    // inner1.show(inner2) inner1、inner2所属对象不同，不能调用，解决办法
    // 类型投影 show(obj:Outer#Inner)，传入Inner时忽略Outer的类型
    inner1.show(inner2)


  }
}

class Outer {

  class Inner {
    //def show(obj: Inner): Any = {}
    def show(obj: Outer#Inner): Any = {}
  }

}