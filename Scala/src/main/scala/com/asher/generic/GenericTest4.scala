package com.asher.generic

case class AA(age:Int)
object AA{
  //比较AA
  implicit val ord: Ordering[AA] = new Ordering[AA] {
    override def compare(x: AA, y: AA): Int = x.age-y.age
  }
}
object GenericTest4 {

  def main(args: Array[String]): Unit = {

    println(max(AA(20), AA(10)))
  }

  def max[T:Ordering](a:T,b:T):T ={
    //召唤出隐式值
    val ord = implicitly[Ordering[T]]
    if (ord.gt(a,b)) a
    else b
  }
}
