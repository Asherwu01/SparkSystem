package com.asher.my_pattern

object Sqrt {
  def unapply(d: Double): Option[Any] = {
    if (d >= 0) Some(math.sqrt(d))
    else None
  }
}

object PatternMatch7 {
  def main(args: Array[String]): Unit = {
    val n: Double = 9
    n match {
      case Sqrt(a) =>
        println(a)
      case _ =>
        println("匹配失败")
    }
  }
}
