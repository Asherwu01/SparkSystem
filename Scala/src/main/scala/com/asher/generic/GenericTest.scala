package com.asher.generic

case class User(age:Int) extends Ordered[User] {
  override def compare(that: User): Int = this.age-that.age
}

object GenericTest {
  def main(args: Array[String]): Unit = {
    println(max(User(18), User(20)))
  }

  def max[T <: Ordered[T]](a:T,b:T):T={ //泛型上界
    if (a > b) a
    else b
  }
}

