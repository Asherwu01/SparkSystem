package com.asher.my_enumeration


object EnumerationTest1 {
  def main(args: Array[String]): Unit = {
    MySeason.spring.show()
  }
}
// sealed 代表它的子类只能出现在当前这个文件中,防止被继承
// Class 换成abstract 或者 trait，防止在外部创建对象
sealed trait Season
object Spring extends Season{
  def show()={
    println("春天")
  }
}
object Summer extends Season
object Autumn extends Season
object Winter extends Season

object MySeason{
  val spring = Spring
  val summer = Summer
  val autumn = Autumn
  val winter = Winter
}
