package com.asher.test

/**
 * Created by VULCAN on 2020/7/1
 *    重点介绍 函数和方法的
 *
 *        区别：
 * 函数和方法的区别：
 *    在面向对象编程中，习惯将解决问题的一段代码，称为方法！
 *    在函数式编程中，习惯将解决问题的一段代码，称为函数！
 *    在本质上没有区别，无非是在思想上，称呼不同！
 *
 * 区别:  形式上有区别！
 *      位置声明在类体中的，称为方法！是类的一个成员，组成部分！
 *          方法可以被重写，重载
 *       在其他位置称为函数！ 声明的位置很灵活！ 注意函数的作用范围！
 *            函数不可以被重写，重载
 *
 *
 *
 *        方法转函数： val 变量名 = 方法名 _
 *                    如果存在重装的方法，要明确声明返回的变量的函数类型！
 *
 *        函数也有类型：  分别为 Function(0-22)
 *                        函数中最多有22个参数，不能超过22！
 */

import  org.junit._
class FunctionAndMethodTest {

  @Test
  def test1() : Unit ={

    val dog = new Dog

    dog.wangwang()

    val son = new DogSon

    son.wangwang()


  }

  @Test
  def test2() : Unit ={

    // 不够灵活
    println(new FunctionAndMethodTest().add(1, 2))

    /*
        将add方法转为函数    方法名 _ 代表将整个方法的定义返回
        可以使用变量接受 方法定义的返回

         (Int, Int) => Int : 代表函数的类型
          (Int, Int) => Int 编译器会自动解析为指定函数的类型

          Function(0 --- 22) : 函数对应的类
              0-22 函数的参数个数



     */
    val function1 :(Int, Int) => Int = add _

    // 出现重载的话要明确函数类型
    val function2 :(Int) => Int = add _

    // 当明确函数类型时，_可以省略
    val function3 :(Int) => Int = add


    // 省略了函数的类型，必须加_,不然引起歧义，编译器会误认为我们要调用函数！
    val function4 = sub _



    println(function1(1, 2))

    println(function2(1))

    println(function3(1))

    // 遗留 true
    // 判断是否是Function2，无法使用到泛型的！
    //  泛型是在编译时，防止类型写错，在编译后，执行时，泛型会被擦除(提升为Object,Any)，
    println(function1.isInstanceOf[Function2[Int,Dog,Dog]])

    println(function1.isInstanceOf[Function1[Int,Dog]])




  }

  //定义方法
  def add(i : Int,j:Int)= i + j


  //重载的add
  def add(i : Int)= i + 1

  def sub(i : Int)= i - 1





}

class Dog{

  // 方法，定义在类体中
  def wangwang()={

    println("wang~wang")

    //函数
    // wuwu() 只能在wangwang()中使用！
    def wuwu()={

      println("wu~")

      // 只能在wuwu()中使用
      def wuwu1()={

        println("wu1~")

      }

      wuwu1()

    }




    //
    //wuwu1()

  }

  // 重载
  def wangwang( name : String)={

    println("冲 着 "+name+" wang~wang")



  }

}

class  DogSon extends  Dog {

  override def wangwang(): Unit =  println(" DogSon wang~wang")

}
