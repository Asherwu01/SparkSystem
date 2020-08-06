package com.asher.my_exetends

class Person(nameParam: String) {

  var name = nameParam
  var age: Int = _

  def this(nameParam: String, ageParam: Int) {
    this(nameParam)
    this.age = ageParam
    println("父类辅助构造器")
  }

  println("父类主构造器")
}
class Emp(nameParam: String, ageParam: Int) extends Person(nameParam, ageParam) {

  var empNo: Int = _

  def this(nameParam: String, ageParam: Int, empNoParam: Int) {
    this(nameParam, ageParam)
    this.empNo = empNoParam
    println("子类的辅助构造器")
  }

  println("子类主构造器")
}

object ExtendsTest3 {
  def main(args: Array[String]): Unit = {
    new Emp("z3", 11,1001)
  }
}
