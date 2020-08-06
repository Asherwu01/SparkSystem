package com.asher.test

/**
 * Created by VULCAN on 2020/7/1
 *
 *    匿名函数
 */

import  org.junit._
class AnonymousFunctionTest {


  /*
        在定义函数时，不关系函数的名称。只关系函数的类型和功能

        在向高阶函数传入函数时，可以不用预先将函数定义好后，再传入，可以直接传入匿名函数！

        匿名函数可以省去def 函数名

        匿名函数不能声明返回值类型，声明就报错！
   */
  @Test
  def test1() : Unit ={

    def add(i:Int,j:Int) :Int = i+j

    //声明一个高阶函数
    def addPlus(a : Int,b:Int, fun: (Int,Int) => Int  ) : Unit ={

      println(fun(a, b) + 1)

    }

    //调用
    addPlus(1 ,2 , add)

    println("-----------------声明一个匿名函数-------------------")

    val fun1=(i:Int,j:Int) => i+j

    println(fun1(1, 2))

    println("-----------------将匿名函数传入到高阶函数-------------------")

    addPlus(1 ,2 , (i: Int,j :Int) => i+j)

  }

  /*
        匿名函数的定义简化：
   */

  @Test
  def test2() : Unit ={

    //定义高阶函数
    def fun1(i:Int , f: Int => Int )=f(i)

    //调用高阶函数
    println(fun1(1, (x: Int) => {x + 100}))

    //如果匿名函数的函数体只有一行代码，可以省略{}
    println(fun1(1, (x: Int) => x + 100))

    // 匿名函数参数类型可以推导，可以省略 参数类型名
    println(fun1(1, (x) => x + 100))

    // 匿名函数参数个数只有一个可以省略 ()
    println(fun1(1, x => x + 100))

    // 匿名函数声明的参数在函数体中(=>后) 只使用了一次，那么可以使用 _ 代替， 摄取x =>
    // _理解为一个占位符，代表函数传入的第一个参数
    println(fun1(1,  _ + 100))


  }

  @Test
  def test3() : Unit ={

    //定义高阶函数
    def fun1(i:Int , f: Int => Int )=f(i)
    /*
         不能简写的情况： 匿名函数的参数在函数体中使用了多次
    */

    println(fun1(1, x => x +  x + 100))

    // Found : (Int,Any) => Int    f: Int => Int
    // 报错的原因是编译器推导的类型和函数定义的类型不匹配
    // 任何简写都要付出代价的！  编译器会把简写还原为完整写法
    //println(fun1(1,  _ + _ + 100))

  }

  @Test
  def test4() : Unit ={

    //定义高阶函数
    def fun1(i:Int ,j:Int , f: (Int,Int) => Int )=f(i,j)
    /*
         不能简写的情况：匿名函数的函数体中参数使用的顺序和声明的顺序不同，也不能简介
    */

    println(fun1(1,2, (x :Int,y:Int) => {
      x + y + 100
    } ))

    println(fun1(1,2, (x :Int,y:Int) => x + y + 100))

    println(fun1(1,2, (x ,y) => x + y + 100))

    println(fun1(1,2,  _ + _ + 100))

    println("----------------------------------------")

    println(fun1(1,2, (x ,y) => y - x + 100)) //101

    println(fun1(1,2,  _ - _ + 100))  //99



  }

  /*
        简写练习
   */

  @Test
  def test5() : Unit = {

    // 高阶函数：  对集合中的每个元素进行 函数计算
    def map(array: Array[Int], f: Int => Unit) = for (elem <- array) f(elem)

    //调用hans
    val array: Array[Int] = Array(1, 2, 3, 4)

    map(array, (x: Int) => {
      println(x + 1)
    })

    //简写
    map(array, x => println(x + 1))

    // 第一种情形
    /*
        Error:(146, 26) missing parameter type for expanded(扩展的)
           println( _ + 1 ) 被还原为了 ：  function ((x$6: <error>) => x$6.$plus(1))
               _ + 1 被还原为了：  (x$6: <error>) => x$6.$plus(1)
               _ 被还原为了：x$6: <error>

               println( _ + 1 ) 早成编译器无法识别_的类型！
     map(array, println( _ + 1 ) )
     */
    // map(array, println( _ + 1 ) )

    println("----------------正常------------------------")

    map(array, x => println( x ))

    println("-----------------println( _ )-----------------------")

    // func就是一个部分函数
    val func: Any => Unit = println(_)

    //  println(_)就是一个匿名的部分函数，和简化无关！
    map(array, println(_) )

    println("-----------------println-----------------------")

    //def println(x: Any) = Console.println(x)   (x:Any) => Unit
    //  f: Int => Unit

    // 普通的传值，不存在任何简写
    map(array,  println)

  }

  /*
      部分函数:  在向一个函数中传入参数时，只传入部分参数，剩下的参数在真正使用时，再传入！
                之传入部分参数的函数，称为部分函数！
   */
  @Test
  def test6() : Unit ={

    // 函数定义
    def add(x:Int,y:Int) :Int = x+y

    //使用，只传入一个
    //  function 就是一个部分函数
    val function: Int => Int = add(1, _)

    println(function(2))


    // 运算一个数的N次幂
    val triple: Double => Double = Math.pow(_, 3)

    println(triple(10))
    println(triple(2))

  }

}
