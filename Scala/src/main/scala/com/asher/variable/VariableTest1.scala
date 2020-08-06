package com.asher.variable

import scala.io.Source

object VariableTest1 {
  def main(args: Array[String]): Unit = {

    val id = 1
    val sql =
      s"""select *
        |from user
        |where
        |id = $id"""".stripMargin
    println(sql)
    println((1 toString) length )
  }
}
