package com.asher.my_io

import java.io.PrintWriter


object OutputTest2 {
  def main(args: Array[String]): Unit = {
    val writer = new PrintWriter("Scala/output/out.txt")
    def createAThread(op: => Unit): Unit ={
      new Thread(new Runnable {
        override def run(): Unit = {
          println("-----------")
          op
        }
      }).start()
    }
    createAThread({

      writer.write("你好")
      writer.close()
    })

  }
}

