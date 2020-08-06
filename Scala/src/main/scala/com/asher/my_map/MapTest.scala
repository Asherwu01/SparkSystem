package com.asher.my_map
/*
1. 创建不可变集合Map
2. 循环打印
3. 访问数据
4. 如果key不存在，返回0
 */
object MapTest {
  def main(args: Array[String]): Unit = {
    // 创建不可变map
    val map = Map("id" -> "1", "name" -> "tome", "gender" -> "male")
    val tuple = "a" -> 1

    // 遍历
    //for (key <- map.keys) {
    //  println(key+" "+ map.get(key).get)
    //}

    for ((k,"tom") <- map) {
      println(k)
    }

    for (("id",v) <- map) {
      println(v)
    }


    for ((k,_) <- map) {
      println(k)
    }

    for (kv <- map) {
      println(kv._1)
      println(kv._2)
    }

    for ((k,v) <- map) {
      println(k+" "+v)
    }

    // 访问数据
    println(map.get("id").getOrElse(0))

    // 循环打印
    map.foreach((kv)=>println(kv))
  }
}
