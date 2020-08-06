package com.asher.my_io

import java.io.PrintWriter


object OutputTest {
  def main(args: Array[String]): Unit = {
    val writer = new PrintWriter("Scala/output/out.txt")
    writer.write("你好")
    writer.close()

  }
}
