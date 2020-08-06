package com.asher.higher_order_function

//wordcount案例
object Demo3 {
  def main(args: Array[String]): Unit = {
    val list = List("hello", "world", "hello", "spark")

    //方式一
    var map = Map[String,Int]()
    list.foreach(x => {
      map += x -> (map.getOrElse(x, 0) + 1)
    })
    println(map)

    //方式二
    val map1 = list.groupBy(x => x).map(kv => kv._1 -> kv._2.length)
    println(map1)

  }
}
