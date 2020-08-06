package com.asher.my_pattern

case class Person(name:String,age: Int)

/*class Person(var name:String,var age: Int)
object Person{
  def unapply(p: Person): Option[(String, Int)] = {
    if (p eq  null) None
    else Some(p.name,p.age)
  }

  def apply(name: String, age: Int): Person = new Person(name, age)
}*/

object PatternMatch8 {
  def main(args: Array[String]): Unit = {
    val p = Person("asher", 12)
    p match {
      case Person(name,age) =>
        println(name+"=="+age)
      case _ =>
        println("match fiald")
    }
  }
}
