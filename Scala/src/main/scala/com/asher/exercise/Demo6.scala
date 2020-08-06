package com.asher.exercise

object Demo6 {
  def main(args: Array[String]): Unit = {
    val e1: Either[Int,String] = Left(-1)
    val e2: Either[Int,String] = Right("正确")

    if (e1.isLeft) {
      println(e1.left.get)
    }

    if (e2.isRight){
      println(e2.right.get)
    }
  }
}
