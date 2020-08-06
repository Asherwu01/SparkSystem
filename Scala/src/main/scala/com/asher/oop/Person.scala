package com.asher.oop

import scala.beans.BeanProperty

class Person {

  var name: String = "bobo" //定义属性

  var age: Int = _ // _表示给属性一个默认值

  //Bean属性（@BeanProperty）
  @BeanProperty var sex: String = "男"
}

object Person {
  def main(args: Array[String]): Unit = {

    var person = new Person()
    println(person.name)

    person.setSex("女")
    println(person.getSex)
  }
}