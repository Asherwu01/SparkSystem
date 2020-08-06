package com.asher.oop

class User(var name: String, val age: Int) {

}

object User1 {
  def main(args: Array[String]): Unit = {
    val user = new User("tom",18)
    println(user.name)
    println(user.age)
    user.name = "jack"
    println(user.name)
  }
}