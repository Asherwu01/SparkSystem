package com.asher.generic

case class CC(age:Int)
object CC{
  implicit def f(cc:CC)=new Ordered[CC] {
    override def compare(that: CC): Int = cc.age-that.age
  }
}
object ViewBound {
  def main(args: Array[String]): Unit = {
    println(max(2, 3))
    println(max(CC(10),CC(20)))
  }

  //视图界定简化
  def max[T <% Ordered[T]](x:T,y:T):T={
    if (x > y) x
    else y
  }

  //隐式转换函数
  /*def max[T](x:T,y:T)(implicit f: T =>Ordered[T]):T={
    if (x > y) x
    else y
  }*/
}
