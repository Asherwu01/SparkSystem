package com.asher.my_map

import scala.collection.mutable

/*
（1）创建可变集合
（2）打印集合
（3）向集合增加数据
（4）删除数据
（5）修改数据

 */
object MapTest2 {
  def main(args: Array[String]): Unit = {
    //创建可变map
    val map = mutable.Map("id" -> 12, "age" -> 20)
    // 打印集合
    for (key <- map.keys) {
      println(key+" "+map.get(key).get)
    }

    // 添加元素，如果有，覆盖
    val maybeInt = map.put("id", 21)
    val i = maybeInt.getOrElse(0)
    println(i)

    map.foreach((kv)=>println(kv))

    // 删除元素
    map.-=("id")

    // 更新元素
    map.update("age",1000)
    map.foreach((kv)=>println(kv))
  }
}
