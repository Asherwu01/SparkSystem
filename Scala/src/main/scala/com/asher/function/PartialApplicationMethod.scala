package com.asher.function

import java.util.Date

import javafx.scene.chart.PieChart.Data

object PartialApplicationMethod {
  def main(args: Array[String]) {
    val date = new Date
    log(date, "hello")
    Thread.sleep(1000)
    log(date, "world")
    Thread.sleep(1000)
    log(date, "scala")

    val log_str = log(date, _)
    log_str("hello,word")
    log_str("hello,word")

    val function = println(_:Any)

    //var f:(date: Date,str: String)=>Unit = log  //方法名不能给其它变量赋值，可以转成函数，再赋值
  }

  def log(date: Date, message: String) = {
    println(date + "----" + message)
  }

}
