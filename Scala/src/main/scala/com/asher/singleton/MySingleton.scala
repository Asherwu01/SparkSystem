package com.asher.singleton

object MySingleton {
  var singleton: MySingleton = new MySingleton()

  def main(args: Array[String]): Unit = {
    val obj = MySingleton()
    val obj2 = MySingleton()
    println(obj.eq(obj2))

  }

  def apply(): MySingleton = singleton
}

class MySingleton {
  def test(): Unit = println("单例对象。。。。")

}