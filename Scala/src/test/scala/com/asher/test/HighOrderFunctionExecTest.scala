package com.asher.test

/**
 * Created by VULCAN on 2020/7/1
 *
 * 练习高阶函数
 * ①编写函数：将一个集合中的元素依次两两(前面运算的结果和后一个元素继续)运算，返回运算后的结果
 * ②编写函数： 将集合中的每一个元素进行遍历，将复合要求的元素保留，是否复合要求可以通过函数进行判断
 * ③编写函数： 将集合中的每一个元素进行遍历，通过自定义函数进行运算后输出结果
 */

import  org.junit._
class HighOrderFunctionExecTest {

  //编写函数：将一个集合中的元素依次两两(前面运算的结果和后一个元素继续)运算，返回运算后的结果
  /*
        定义函数时，参数列表中必须写参数名！

        (Int,Int) => Int： 描述函数类型的！
   */
  @Test
  def test1() : Unit ={

    /*
              for (i <- array) : i是数组中的每个元素

           java：   for(int i = 0 ;i<array.length;i++) : i是索引，使用索引获取数组中对应的元素！  array[i]

            scala:  for(i <- 0 until array.length ) : i是索引，使用索引获取数组中对应的元素！  array(i)

            []: 放泛型
            {}:  代码
            ():

         */
    def reduce ( array : Array[Int] , f: (Int,Int) => Int ) : Int={

      // 定义变量保存结果,  赋值array(0)是为了让result参与运算
      var result = array(0)

      for(i <- 1 until array.length ){
        result = f(result,array(i) )
      }

      result
    }

    //调用reduce
    println(reduce(Array(1, 2, 3, 4), (x: Int, y: Int) => x + y))

    println(reduce(Array(1, 2, 3, 4), (x: Int, y: Int) => x * y))

    //简化
    println(reduce(Array(1, 2, 3, 4),  _ * _))

  }

  //编写函数： 将集合中的每一个元素进行遍历，将符合要求的元素保留，是否复合要求可以通过函数进行判断
  @Test
  def test2() : Unit ={

    def filter ( array : Array[Int] , f: Int => Boolean ) : Array[Int]={

      for (elem <- array  if (f(elem))) yield elem

    }

    // 调用
    val array: Array[Int] = Array(1, 2, 3, 4)

    //mkString(",")： 将数组中的每个元素使用 ,分割，转为一个字符串
    println(filter(array, (x: Int) => x > 2).mkString(","))

    //简化
    println(filter(array,  _ > 2) mkString "," )


  }

  //编写函数： 将集合中的每一个元素进行遍历，通过自定义函数进行运算后输出结果
  @Test
  def test3() : Unit ={

    def map ( array : Array[Int] , f: Int => Any ) : Array[Any]={

      for (elem <- array  ) yield f(elem)

    }
    // 调用
    val array: Array[Int] = Array(1, 2, 3, 4)

    println(map(array, (x: Int) => x + 1).mkString(","))

    println(map(array, _ + 1).mkString(","))

    map(array, (x: Int) => println("男宾"+x+"位！"))

    //简化
    //map(array,  println("男宾"+ _ +"位！"))




  }

}
