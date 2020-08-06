package com.asher.exercise

object PrimeNum {
  def main(args: Array[String]): Unit = {
    // 使用函数计算1-1000内的所有质数
    val sum = sumPrime(1, 1000);
    println(sum)

  }

  // 求1-n中质数和
  def sumPrime(from: Int, to: Int): Int = {
    var sum = 0;
    for (i <- from to to if isPrime(i)) sum += i
    sum
  }

  // 判断是否是质数
  def isPrime(i: Int): Boolean = {
    if (i < 2) return false
    else {
      for(j <- 2 until i){
        if(i%j==0) return false
      }
    }
    return true
  }
}
