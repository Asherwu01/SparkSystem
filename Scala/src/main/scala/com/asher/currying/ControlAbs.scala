package com.asher.currying

object ControlAbs {

  def runInThread(code: => Unit) = {
    new Thread() {
      override def run(): Unit = code
    }.start()
  }

  def main(args: Array[String]): Unit = {
    println(Thread.currentThread().getName)
    runInThread {
      println(Thread.currentThread().getName)
    }

    // 不是类的成员，叫函数
    def say()={
      println("在说话")
    }

    var f: ()=>Unit = say
    ()=>{println("adfad")}
  }

  //不是类的成员，叫函数
  def say()={
    println("在说话")
  }

  def say1():Unit={
    println("在说话")
  }

}

class User{
  // 是类的成员，叫方法，不叫函数
  def say()={
    println("在说话")
  }

  //类中不能定义函数,注意 =>有两个作用，一个是定义匿名函数，一个是代表 函数类型
  //def son(): Unit =>{  //不能这样定义
  //  println("在唱歌")
  //}
}