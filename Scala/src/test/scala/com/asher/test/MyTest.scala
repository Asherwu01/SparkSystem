package com.asher.test

import org.junit.Test


class MyTest {

  @Test
  def test1(): Unit = {
    val i = 10;
    println(i)
    val j =10

  }

  @Test
  def main(): Unit = {
    val i = 10;
    println(i)
    val j =10

  }

  @Test
  def test2(): Unit = {
   for {i <- 1 to 4} print(i+" ")
  }


}
