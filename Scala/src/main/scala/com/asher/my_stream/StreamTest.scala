package com.asher.my_stream

object StreamTest {
  def main(args: Array[String]): Unit = {
    /*val list = List(10, 30, 20, 1, 2, 3, 4)
    //惰性数据结构，使用时加载
    val stream = list.toStream

    println(stream)
    println(stream.head)
    println(stream.tail)
    println(stream.force)//强制加载*/

    def getS:Stream[Int] = {
      1 #:: getS
    }

    val s: Stream[Int] = getS
    println(s.take(5).force)

    //计算斐波数列
    def fibSeq(n: Int): List[Int] = {
      def go(a: Int, b: Int): Stream[Int] = {
        a #:: go(b, a + b)
      }

      go(1, 1).take(n).force.toList

    }

    def fibSeq2(n: Int): List[Int] = {
      def go2: Stream[Int] = {
        1 #:: go2.scanLeft(1)(_ + _)
      }

      go2.take(n).force.toList

    }

    println(fibSeq2(5))
  }
}
