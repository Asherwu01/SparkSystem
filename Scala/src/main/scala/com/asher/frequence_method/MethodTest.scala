package com.asher.frequence_method

object MethodTest {
  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3, 4, 5)
    //（1）获取集合长度
    println(list.length)

    //（2）获取集合大小
    println(list.size)

    //（3）循环遍历
    list.foreach(x => print(x+" "))
    println()

    //（4）迭代器
    for (elem <- list.iterator) {
      println(elem)
    }

    //（5）生成字符串
    println(list.mkString(","))

    //（6）是否包含
    println(list.contains(3))


  }
}
