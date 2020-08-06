package com.asher.sequence

object ArraySeq{
  def unapplySeq(s:String): Option[List[String]] ={
    if (s==null)None
    else Some(s.split(",").toList)
  }
}

object SequenceTest {
  def main(args: Array[String]): Unit = {
    val name = "zhangsan,lisi,wangwu"
    name match {
      case ArraySeq(one,two,rest@_*) =>
        println(one)
        println(two)
        println(rest)
    }

  }
}
