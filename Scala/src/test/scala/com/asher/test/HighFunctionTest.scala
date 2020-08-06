package com.asher.test

/**
 * Created by VULCAN on 2020/7/1
 *
 *    高阶(等)函数：  当一个函数的参数列表表声明有函数类型的参数，这个函数就是高阶函数
 *
 *
 *      阶级
 */

import  org.junit._

class HighFunctionTest {


  @Test
  def test1() : Unit ={

    //声明一个普通函数： 参数列表中没有函数类型
    def add(a : Int,b:Int) : Int = a + b

    def sub(a : Int,b:Int) : Int = a - b

    def mul(a : Int,b:Int) : Int = a - b

    //声明一个高阶函数
    def addPlus(a : Int,b:Int, fun: (Int,Int) => Int  ) : Unit ={

      //剥削fun，让你为我干活
      println(fun(a, b) + 1)

    }
    //调用
    addPlus(1 ,2 , add)

    // 高阶函数可以让函数式编程更灵活，类比解耦原则！
    addPlus(1 ,2 , sub)

  }

}
