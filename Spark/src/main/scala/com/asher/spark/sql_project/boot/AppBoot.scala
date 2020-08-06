package com.asher.spark.sql_project.boot

import com.asher.spark.sql_project.udaf.{MyNewUDAF, MyUDAF}
import org.apache.spark.sql.{SparkSession, functions}

object AppBoot {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("sql")
      .enableHiveSupport()
      .config("spark.sql.warehouse.dir", "hdfs://hadoop102:9820/user/hive/warehouse")
      .getOrCreate()

    spark.sql("use sparksql")

    //spark 2.x 的udaf
    //创建函数
    //val myUDAF = new MyUDAF
    //注册
    //spark.udf.register("myUDAF",myUDAF)

    //spark 3.x 的udaf
    //创建
    val myNewUDAF = new MyNewUDAF
    //注册并使用
    spark.udf.register("myNewUDAF",functions.udaf(myNewUDAF))

    val sq11 =
      """
        |select ci.city_id,ci.area,ci.city_name,pi.product_id, pi.product_name
        |        from user_visit_action uva join city_info ci on uva.city_id=ci.city_id
        |        join product_info pi on uva.click_product_id=pi.product_id
        |""".stripMargin


    val sql2 =
      """
        |select area,product_name,count(*) clickCount,myNewUDAF(city_name) otherCity
        |        from t1
        |        group by area,product_id,product_name
        |""".stripMargin


    val sql3 =
      """
        |select area,product_name,clickCount,otherCity,rank() over(partition by area order by clickCount desc) rn
        |        from t2
        |""".stripMargin

    //"区域"  "产品名称" "点击次数" "城市备注"
    val sql4 =
      """
        |select area,product_name,clickCount,otherCity
        |        from t3
        |        where rn <= 3
        |""".stripMargin


    spark.sql(sq11).createTempView("t1")
    spark.sql(sql2).createTempView("t2")
    spark.sql(sql3).createTempView("t3")
    spark.sql(sql4).show(false)


    spark.stop()

  }
}
