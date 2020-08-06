package com.asher.spark.stream_project.app

import com.asher.spark.stream_project.bean.AdsInfo
import com.asher.spark.stream_project.util.JDBCUtil
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable.ListBuffer

/**
 * 需求：
 * 实现实时的动态黑名单机制：将每天对某个广告点击超过 100 次的用户拉黑。
 * 分析；
 * AdsInfo(1595575286651,华南,深圳,103,1,2020-07-24,15:21)
 * 算法：
 *      1.  创建2张表
 * user_ad_count: 自己维护状态，保存某个用户当天对每个广告的点击行为，并不断累加点击次数
 * black_list： 黑名单表，将当天对某个广告点击超过 100 次的用户加入黑名单表
 *      2.  统计这个批次中，每个用户，点击每个广告的次数
 *      3.  判断当前用户是否在黑名单中，已经在的，不需要在统计了
 *      4.  数据保存到user_ad_count中，累加点击次数
 *      5.  保存后，全表扫描user_ad_count，获取大于100次的用户，添加到black_list(采用主键重复则覆盖)
 *
 */
object Function1Boot extends BaseApp {

  def main(args: Array[String]): Unit = {
    runApp {

      //从kafka中读取数据，封装为AdsInfo(1595575286651,华南,深圳,103,1,2020-07-24,15:21)
      val ds: DStream[AdsInfo] = getAllBeans(getDataFromKafka())

      //统计这个批次，每个用户，点击每个广告，总的次数
      val ds1: DStream[((String, String, String), Int)] = ds.map(adsInfo => ((adsInfo.dayString, adsInfo.userId, adsInfo.adsId), 1))
        .reduceByKey(_ + _)

      //判断当前用户是否在黑名单中，已经在的，不需要在统计了, ----获取要保存状态的数据
      val ds2 = ds1.filter {
        case ((dayString, userId, adsId), count) => !getBlackList().contains(userId)
      }


      //数据保存到user_ad_count中，累加点击次数，------ 将数据的状态保存到数据库，
      ds2.print(100)
      ds2.foreachRDD(rdd => {
        rdd.foreachPartition(iter => {
          //已分区为单位保存数据库
          //创建连接
          val connection = JDBCUtil.getConnection()

          //准备sql
          var sql =
            """
              |INSERT INTO `user_ad_count` VALUES(?,?,?,?)
              |ON DUPLICATE KEY UPDATE COUNT = COUNT + ?
              |""".stripMargin

          //获取预处理Statement
          val ps = connection.prepareStatement(sql)

          //给占位符赋值
          iter.foreach {
            case ((day, userId, adsId), count) =>
              ps.setString(1, day)
              ps.setString(2, userId)
              ps.setString(3, adsId)
              ps.setInt(4, count)
              ps.setInt(5, count)

              ps.executeUpdate()
          }
          ps.close()

          //此时，状态已经保存了
          //接下来，全表扫描user_ad_count,超过100的用户添加到黑名单。
          var sql1 =
          """
            |select userid
            |from user_ad_count
            |where count>100
            |""".stripMargin
          val ps1 = connection.prepareStatement(sql1)
          //查出超过100的用户
          val rs = ps1.executeQuery()


          val blackList: ListBuffer[String] = ListBuffer[String]()//用来保存黑名单数据
          //将超过100的用户加入黑名单
          while (rs.next()){blackList.append(rs.getString("userid"))}
          rs.close()
          ps.close()


          //遍历blackList,将黑名单加入black_list表中
          val sql2 =
            """
              |INSERT INTO `black_list` VALUES(?)
              |ON DUPLICATE KEY UPDATE userid = ?
              |""".stripMargin
          val ps2 = connection.prepareStatement(sql2)
          blackList.foreach( userid => {
            ps2.setString(1, userid)
            ps2.setString(2, userid)
            ps2.executeUpdate()
          })

          //关闭资源
          ps2.close()
          connection.close()
        })
      })

    }
  }

  /*
      获取已经在黑名单中的用户，返回可变的List
   */
  def getBlackList(): ListBuffer[String] = {
    val connection = JDBCUtil.getConnection()

    val sql =
      """
        |select userid from black_list
        |""".stripMargin
    val ps = connection.prepareStatement(sql)
    val rs = ps.executeQuery()

    val blackList = ListBuffer[String]()
    while (rs.next) {
      blackList.append(rs.getString("userid"))
    }

    rs.close()
    ps.close()
    connection.close()

    blackList

  }
}
