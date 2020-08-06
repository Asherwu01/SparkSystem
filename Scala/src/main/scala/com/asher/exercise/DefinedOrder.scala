package com.asher.exercise

object DefinedOrder {
  def main(args: Array[String]): Unit = {
    // name 升序，age 降序，sex 升序
    val persons = List(Person("jack", 18, '0'), Person("amy", 20, '0'), Person("jack", 17, '0'),
      Person("tom", 17, '0'), Person("tom", 17, '1'))
    //sortBy List(amy  20   0, jack  18   0, jack  17   0, tom  17   0, tom  17   1)
    val res1 = persons.sortBy(p => p)(new MyOrder)

    //sortWith
    val res2 = persons.sortWith((p1, p2) => {
      if (p1.name != p2.name) p1.name < (p2.name) //升序
      else {
        if (p1.age != p2.age) p1.age > p2.age //降序
        else p1.sex < p2.sex
      }
    })


    println(res1)
    println(res2)
  }
}

class MyOrder extends Ordering[Person] {

  override def compare(x: Person, y: Person): Int = {
    if (x.name.compareTo(y.name) != 0) x.name.compareTo(y.name)
    else {
      if (x.age-y.age!=0) y.age-x.age
      else {
        x.sex.compareTo(y.sex)
      }
    }
  }
}

class Person(var name: String, var age: Int, var sex: Char) {
  override def toString()= s"$name  $age   $sex"
}

object Person {
  def apply(name: String, age: Int, sex: Char): Person = new Person(name, age, sex)
}