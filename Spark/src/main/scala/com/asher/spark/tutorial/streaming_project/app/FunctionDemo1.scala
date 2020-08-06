package com.asher.spark.tutorial.streaming_project.app

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.asher.spark.tutorial.streaming_project.bean.AdsInfo
import com.asher.spark.tutorial.streaming_project.util.JDBCUtil
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable.ListBuffer

/**
 * Created by VULCAN on 2020/7/24
 *
 * 实现实时的动态黑名单机制：将每天对某个广告点击超过 100 次的用户拉黑。
 * 注：黑名单保存到MySQL中。
 *
 * user_ad_count: 自己维护状态
 * black_list： 黑名单表
 *
 * 逻辑：  AdsInfo(1595575286651,华南,深圳,103,1,2020-07-24,15:21)
 *          ①统计这个批次中，每个用户，点击每个广告的次数
 *
 *          ③已经在黑名单中的用户，不需要再记录状态了
 *
 *          ③保存状态，将这个批次用户点击的数据 写到  user_ad_count 表
 *
 *          ④查询累计后的状态，看count > 100的用户有哪些
 *
 *          ⑤拉黑④查询的用户
 *
 */
object FunctionDemo1 extends BaseApp {

  def main(args: Array[String]): Unit = {

    runApp{

      val ds: DStream[AdsInfo] = getAllBeans(getDataFromKafka)

      // 统计这个批次中，每个用户，点击每个广告的次数
      val ds1: DStream[((String, String, String), Int)] = ds.map(adsInfo => ((adsInfo.dayString, adsInfo.userId, adsInfo.adsId), 1))
        .reduceByKey(_ + _)

      // 将已经在黑名单中的用户，进行过滤，不需要再记录状态了
      val blackList: ListBuffer[String] = getBlackList()

      // 要保存状态的数据
      val ds2: DStream[((String, String, String), Int)] = ds1.filter {
        case ((day, userid, adsid), count) => !blackList.contains(userid)
      }

      // 将数据保存状态，写入user_ad_count 表
      ds2.foreachRDD(rdd =>{

        rdd.foreachPartition(iter=>{
          // 创建连接
          val connection: Connection = JDBCUtil.getConnection()
          //准备sql
          val sql =
            """
              |INSERT INTO `user_ad_count` VALUES(?,?,?,?)
              |ON DUPLICATE KEY UPDATE COUNT=COUNT + ?
              |
              |""".stripMargin

          //获取ps
          val ps: PreparedStatement = connection.prepareStatement(sql)

          iter.foreach{
            case ((day, userid, adsid), count) =>{

              ps.setString(1,day)
              ps.setString(2,userid)
              ps.setString(3,adsid)
              ps.setInt(4,count)
              ps.setInt(5,count)

              ps.executeUpdate()
            }

              // 查询哪些人已经超过次数
              val sql1 =
                """
                  |select userid from user_ad_count
                  |where count >= 100
                  |
                  |""".stripMargin

              //获取ps
              val ps1: PreparedStatement = connection.prepareStatement(sql1)

                val rs: ResultSet = ps1.executeQuery()

              val blackList: ListBuffer[String] = ListBuffer[String]()

              while(rs.next()) { blackList.append(rs.getString("userid"))}

              rs.close()

              ps1.close()

              // 加入到黑名单

              val sql2=
                """
                  |insert into black_list values(?)
                  |ON DUPLICATE KEY UPDATE userid=?
                  |
                  |""".stripMargin

              val ps2: PreparedStatement = connection.prepareStatement(sql2)

              blackList.foreach(userid => {
                ps2.setString(1,userid)
                ps2.setString(2,userid)

                ps2.executeUpdate()
              })

              ps2.close()

          }
          //关闭资源
          ps.close()
          connection.close()
        })
      })


    }

  }

  def getBlackList():ListBuffer[String]={

    val connection: Connection = JDBCUtil.getConnection()

    val sql = "select userid from black_list"

    val ps: PreparedStatement = connection.prepareStatement(sql)

    val rs: ResultSet = ps.executeQuery()

    val blackList: ListBuffer[String] = ListBuffer[String]()

    while(rs.next()) { blackList.append(rs.getString("userid"))}

    rs.close()
    ps.close()
    connection.close()

    blackList


  }



}
