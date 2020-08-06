package com.asher.spark.tutorial.sql_project.main


import com.asher.spark.tutorial.sql_project.udaf.MyNewUDAF
import org.apache.spark.sql.{Dataset, SparkSession, functions}

/**
 * Created by VULCAN on 2020/7/21
 */
object MainApp {

  def main(args: Array[String]): Unit = {

    val spark: SparkSession = SparkSession.builder().
      master("local[*]").
      appName("df").
      enableHiveSupport().
      config("spark.sql.warehouse.dir","hdfs://hadoop102:9820/user/hive/warehouse")
      .getOrCreate()

    import  spark.implicits._


    val sql1=
      """
        |
        |select
        |   ci.*,
        |   p.product_name,
        |   uv.click_product_id
        | from  user_visit_action uv join  city_info ci on uv.city_id=ci.city_id
        | join product_info p on uv.click_product_id=p.product_id
        |""".stripMargin

    //注册
    // 2.0 API
    //val myUDAF = new MyUDAF

    val myNewUDAF = new MyNewUDAF

   // 注册2.0
   // spark.udf.register("myudaf",myUDAF)
    // 注册新api
    spark.udf.register("myudaf",functions.udaf(myNewUDAF))


    val sql2=
      """
        | select
        |     area,product_name,count(*) clickCount, myudaf(city_name) result
        |  from t1
        |  group by area,click_product_id,product_name
        |""".stripMargin

    val sql3=
      """
        |
        |select  area,product_name,clickCount,result, rank() over(partition by area order by clickCount desc) rn
        |  from t2
        |
        |""".stripMargin

    val sql4=
      """
        | select
        |    area,product_name,clickCount,result
        |  from t3
        |  where rn <=3
        |
        |""".stripMargin

    //切库

    //spark.sql("select * from emp").show()
    spark.sql("use sparksql")



    spark.sql(sql1).createTempView("t1")
    spark.sql(sql2).createTempView("t2")
    spark.sql(sql3).createTempView("t3")
    spark.sql(sql4).show(false)

    spark.close()


  }

}
