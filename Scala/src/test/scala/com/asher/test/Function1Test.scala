package com.asher.test

/**
 * Created by VULCAN on 2020/7/1
 *    介绍函数式编程中函数的声明，调用，注意事项
 *
 *  函数式编程： 将解决问题的每一个步骤，封装为函数。
 *              解决什么问题就调用什么函数！
 *                调用函数时，可以传入参数，获取函数的返回值！
 *                将某个函数的返回值，继续作为参数，传入另一个函数，继续执行！
 */

import  org.junit._
class Function1Test {

  /*


          函数的定义：
                def  函数名 [( [参数列表 ])] [[: 返回值类型] =]  { }

                []: 可以省略不写

           函数的调用：   函数名(参数列表)


   */

  /*
      完整的函数
   */
  @Test
  def test1() : Unit ={

    def add(a : Int,b:Int) : Int ={
      a + b
    }

    println(add(1, 2))

  }

  /*
     省略： [参数列表 ]
  */
  @Test
  def test2() : Unit ={

    def add() : Int ={
      1
    }

    println(add())
    println(add)

  }

  /*
    省略： [(参数列表) ]
 */
  @Test
  def test3() : Unit ={

    def add : Int ={
      1
    }

    // 如果没有 ()，在调用时不能写，替换访问的统一性
    // println(add())  not ok
    println(add)

  }

  /*
     [: 返回值类型]
        在scala中，声明类型参数的地方，基本都可以省略，由编译器推导！
            在复杂场景(递归)，必须明确声明！
  */
  @Test
  def test4() : Unit ={

    // 由编译器推导
    def add()  ={
      1
    }

    def add1()  ={
      println("hello")
    }

    println(add())
    println(add)

  }

  @Test
  def test5() : Unit ={

    // 明确声明Unit后，函数体中的最后一行，就不会被返回了！  写return 也不行！
    def add() :Unit  ={
      return 1
    }

    println(add())

  }

  /*
      省略： [[: 返回值类型] =]

          将省略了返回值类型和 =赋值运算的函数称为 过程(procedure)
   */
  @Test
  def test6() : Unit ={

    def add() {
      return 1
    }

    println(add())

  }

  /*
      存在递归调用的函数,必须明确声明返回值类型！
            必须声明的原因： 编译器无法推导！
   */
  @Test
  def test7() : Unit ={

    //阶乘
    def jiecheng( i : Int) :Int  ={

      if ( i <= 1 ) 1
      else{
         i * jiecheng(i - 1)
      }

    }

  }



}
