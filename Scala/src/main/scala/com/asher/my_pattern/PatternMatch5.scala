package com.asher.my_pattern

object PatternMatch5 {
  def main(args: Array[String]): Unit = {
    //val t:Any = ("tom",20)
    /*val t:Any = (1,(2,(3,(4,5))))
    t match {
      /*case (a,b) =>
        println(a)
        println(b)*/

      /*case (a:String,b) =>
        println(a)
        println(b)*/

      case (_,(_,(_,(_,a)))) =>
        println(a)
    }*/

    val map = Map("a" -> 97, "b" -> 98)
    map.foreach(kv =>{
      kv match {
        case (k,v) =>
          println(k)
          println(v)
      }
    })
  }
}
