package com.asher.generic
class User11{
   val array =new Array[Int](10)

  def apply(index :Int): Int = array(index)

  def update(index:Int,data:Int)={
    array(index) = data
    println(array(index))
  }
}
object Demo2 {
  def main(args: Array[String]): Unit = {
    val user = new User11
    user(0) = 12
  }
}
