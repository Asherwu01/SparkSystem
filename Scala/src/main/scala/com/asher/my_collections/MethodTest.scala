package com.asher.my_collections

object MethodTest {
  def main(args: Array[String]): Unit = {

    def fun(str: String) = {
      //val fun1: (String, String) => String = (s1: String, s2: String) => {
      //  s1 + s2 + str
      //}

      def fun1(s1: String,s2: String) = {
        s1+s2+str
      }
      fun1 _
    }

    val function = fun("aaa")
    val str = function("hello", "world")
    println(str)

    def fun2(s: String): Unit = {
     println(s)
    }
  }
}
