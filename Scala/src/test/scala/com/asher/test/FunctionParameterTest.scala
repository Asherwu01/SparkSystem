package com.asher.test

/**
 * Created by VULCAN on 2020/7/1
 *
 *  介绍函数的参数的声明，传值等！
 *
 *        可变参数：   Java中  参数类型...参数名
 *                                本质是数组
 *                    scala   参数名 ： 参数类型*
 *                                 本质是数组
 *
 *        默认参数 ：  在函数的参数列表中，可以给某些参数直接赋默认值！
 *
 */

import  org.junit._

class FunctionParameterTest {

  /*
      可变参数的简单示例
   */
    @Test
    def test1() : Unit ={

      // 传入的多个 Int的参数，会放入到一个WrappedArray[Int],赋值给x
      def add(x: Int*)={

       // for (elem <- x) println(elem)

        //求和
        println(x.sum)

      }

      //使用
      add(1, 2, 3)

      println("-------------------------")

      add(1, 2, 3, 4, 5)

    }


  @Test
  def test2() : Unit ={

    // 可变参数必须放在最后
    def add(s1 : String ,s2 : String,x: Int* )={

      // for (elem <- x) println(elem)

      //求和
      println(x.sum)

    }

  }

  /*
     默认参数
   */
  @Test
  def test3() : Unit ={

    def sayHello(name : String = "jack")= println("hello:" + name)

    // 函数的默认参数可以不传值
    sayHello()

    // 传值会覆盖默认参数的值
    sayHello("tom")

  }

  /*
        传参时，可以使用带名参数

            如果不不想使用带名参数，可以将默认参数后置！
   */
  @Test
  def test4() : Unit ={

    def sayHello(name : String = "jack", age : Int, gender : String)= printf("hello : %s, age: %d ,gender: %s:" , name,age,gender )

    // 完整传入
    sayHello("tom",20,"男")

    // 传参时，需要按照顺序赋值
    // 带名参数后，不能再写参数，带名参数要后置
    sayHello(gender="男",age=20)


    //如果不不想使用带名参数，可以将默认参数后置！
    def sayHello1( age : Int, gender : String,name : String = "jack")= printf("hello : %s, age: %d ,gender: %s:" , name,age,gender )

    sayHello1(20,"男")


  }

}
