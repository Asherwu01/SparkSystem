package com.asher.spark.tutorial.streaming_project.app

import java.sql.{Connection, PreparedStatement}

import com.asher.spark.tutorial.streaming_project.bean.AdsInfo
import com.asher.spark.tutorial.streaming_project.util.JDBCUtil
import org.apache.spark.streaming.dstream.{DStream, InputDStream}

/**
 * Created by VULCAN on 2020/7/24
 *
 * 实时统计每天各地区各城市各广告的点击总流量，并将其存入MySQL
 *    有状态的计算
 *
 *    AdsInfo(1595575286651,华南,深圳,103,1,2020-07-24,15:21) =>  ((华南,深圳,1) ,1)
 *
 *    ((华南,深圳,1) ,1) =>  ((华南,深圳,1) ,N)
 *          reduceByKey: 无状态
 *           updateStateByKey:
 *
 *
 *     遇到主键冲突，可以使用：
 * INSERT INTO `area_city_ad_count` VALUES('2020-07-28','华南','深圳',1,10)
 * ON DUPLICATE KEY UPDATE COUNT=VALUES(COUNT)
 */
object FunctionDemo2 extends BaseApp {

  def main(args: Array[String]): Unit = {

    runApp{

      //ck
      context.checkpoint("function2")

      // 从kafka中读取数据，封装为 AdsInfo
      //AdsInfo(1595575286651,华南,深圳,103,1,2020-07-24,15:21)
      val ds: DStream[AdsInfo] = getAllBeans(getDataFromKafka)

      // 结果  DStream[  N 个 RDD]
      //  RDD1 :  ((2020-07-30,华南,深圳,1) ,5)      RDD2 :  ((2020-07-30,华南,深圳,1) ,10)
      val result: DStream[((String,String, String, String), Int)] = ds.map(adsInfo => ((adsInfo.dayString,adsInfo.area, adsInfo.city, adsInfo.adsId), 1)).
        updateStateByKey((seq: Seq[Int], opt: Option[Int]) => Some(seq.sum + opt.getOrElse(0)))

      //将结果写入到mysql
      result.foreachRDD(rdd => {

        // 一个分区作为一个整体处理，一个分区只需要创建一个Connection
        rdd.foreachPartition(iter=>{

          // 创建连接
          val connection: Connection = JDBCUtil.getConnection()
          //准备sql
          val sql =
            """
              |INSERT INTO `area_city_ad_count` VALUES(?,?,?,?,?)
              |ON DUPLICATE KEY UPDATE COUNT=?
              |""".stripMargin

          //获取ps
          val ps: PreparedStatement = connection.prepareStatement(sql)

          iter.foreach{
            case ((day,area, city, ads), count) =>{

              ps.setString(1,day)
              ps.setString(2,area)
              ps.setString(3,city)
              ps.setString(4,ads)
              ps.setInt(5,count)
              ps.setInt(6,count)

              ps.executeUpdate()

            }

          }

          //关闭资源
          ps.close()
          connection.close()

        })
      })

      ds.print(100)


    }


  }

}
