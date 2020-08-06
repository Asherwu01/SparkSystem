package com.asher.exercise

import com.asher.my_exetends.Animal

class Dog extends Animal{

}

object Test extends Animal {
  def main(args: Array[String]): Unit = {
    val dog = new Dog
    super.say()
  }
}