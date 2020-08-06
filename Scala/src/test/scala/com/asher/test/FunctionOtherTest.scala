package com.asher.test

/**
 * Created by VULCAN on 2020/7/1
 */

import  org.junit._

class FunctionOtherTest {

  // 函数的嵌套调用
  @Test
  def test1() : Unit ={

    // 函数的嵌套调用
    def fun1(i:Int,j:Int ,f1 : Int => Int,f2:(Int,Int)=>Int,f3:(Int,Int,Int)=>Int )=f3(f2(f1(i),j),i,j)

    // 使用
    println(fun1(1, 2,  _ + 1 , _ * _ , _ - _ - _ ))

  }

}
