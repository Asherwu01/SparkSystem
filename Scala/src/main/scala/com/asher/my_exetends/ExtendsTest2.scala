package com.asher.my_exetends

object ExtendsTest2 {
  def main(args: Array[String]): Unit = {
    val stu = new Student("tom")
    val person: Person1 = stu
    println(person.name)

  }
}

class Person1(val name: String) {}

class Student(override val name: String) extends Person1(name) {
}

