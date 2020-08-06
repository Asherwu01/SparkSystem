package com.asher.exercise

object Demo5 {
  def main(args: Array[String]): Unit = {
    val option = Some(10)
    val option1 = None

    val opt = foo()
    if (opt.isDefined) {
      println(opt.get)
    }
  }
  def foo():Option[User]={
    //Some(new User("asher",25))
    None
  }

}

class User(var name:String,var age: Int){

  override def toString = s"User($name, $age)"
}