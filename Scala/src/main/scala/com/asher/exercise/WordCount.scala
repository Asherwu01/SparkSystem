package com.asher.exercise

import scala.io.Source

object WordCount {
  def main(args: Array[String]): Unit = {
    // 计算单词排名top2

    // 1. 从文件读取数据，将数据封装成 List("一行的内容","一行的内容")
    val source = Source.fromFile("Scala/input/word.txt")
    val iterator = source.getLines()
    val list = iterator.toList

    // 2. 对数据flatMap 变成 List("hello","java",...)
    val words = list.flatMap(str => str.split(" "))

    // 3. groupBy 变成 Map("hello"->List("hello","hello")),....)
    val wordMap = words.groupBy(x => x)

    // 4. map 变成 Map("hello"->4,...)
    val resMap = wordMap.map(kv => kv._1 -> kv._2.size)

    // 5. sortBy 变成 Map("hello"->4,"java"->1,...)
    val tuples = resMap.toList.sortBy(kv => kv._2)(Ordering.Int.reverse)
    println(tuples)
    // 6. 取出top2
    println(tuples.take(2))
  }
}
