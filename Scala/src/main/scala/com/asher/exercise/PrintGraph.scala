package com.asher.exercise

/*           空格    *
    *         4     1
   ***        3     3
  *****       2     5
 *******      1     7
*********     0     9

 */
object PrintGraph {
  def main(args: Array[String]): Unit = {
    funGraph(5)
    funGraph2(5)

    var a = 10
    var b = 20
    var c = a = b
    println(c)

    for (i <- 1 to 3; j <- 1 to 3) print(i * j)
  }

  /*def funGraph(n: Int):Unit = {
    for (i <- 0 to n-1) {
      //打印空格
      print(" "*(4-i))

      //打印 *
      print("*"*(2*i+1))
      println()
    }
  }*/

  def funGraph(n: Int): Unit = {
    //for (i <- 0 to n - 1) print(" " * (4 - i) + "*" * (2 * i + 1) + '\n')
  }

  def funGraph2(n: Int) {
    //for (i <- 0 to n - 1)
    //print(" " * (4 - i) + "*" * (2 * i + 1) + '\n')
  }
}
