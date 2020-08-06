package com.asher.my_exetends



object MyWhile {
  def main(args: Array[String]): Unit = {
    var i =0
    myWhile(i<5){
      println(i)
      i+=1
    }
  }

  def myWhile(op1: =>Boolean)(op2: =>Unit):Any = {
    if(op1) {
      op2
      myWhile(op1)(op2)
    }


  }

}
