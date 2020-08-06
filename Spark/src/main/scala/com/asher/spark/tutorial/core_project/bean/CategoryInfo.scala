package com.asher.spark.tutorial.core_project.bean

/**
 * Created by VULCAN on 2020/7/18
 */
case class CategoryInfo(id:String, var clickCount:Int, var orderCount:Int, var payCount:Int ){

  override def toString: String = id+","+clickCount+","+orderCount+","+payCount

}
