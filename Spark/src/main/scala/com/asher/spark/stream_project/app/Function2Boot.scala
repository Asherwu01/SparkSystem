package com.asher.spark.stream_project.app

import com.asher.spark.stream_project.bean.AdsInfo
import com.asher.spark.stream_project.util.JDBCUtil
import org.apache.spark.streaming.dstream.DStream

/*
    需求二：
        实时统计 每天 各地区 各城市 各广告的 点击总流量，并将其存入MySQL。
    kafka中的msg: 1595587573041,华北,北京,104,5

    分析：后一批的数据依赖于前一批的结果，使用有状态转换。
    AdsInfo(1595575286651,华南,深圳,103,1,2020-07-24,15:21) =>  ((华南,深圳,1) ,1)
         ((华南,深圳,1) ,1) =>  ((华南,深圳,1) ,N)
            reduceByKey:无状态转换，不行，因为跨批次合并，必须保存上一个批次的结果，checkpoint，并且使用有状态转换
            updateStateByKey:有状态转换，合并值
            def updateStateByKey[S: ClassTag](
                updateFunc: (Seq[V], Option[S]) => Option[S]
              ): DStream[(K, S)]

     遇到主键冲突，可以使用：
        INSERT INTO `area_city_ad_count` VALUES('2020-07-28','华南','深圳',1,10)
        ON DUPLICATE KEY UPDATE COUNT=VALUES(COUNT)  //VALUES(COUNT)代表取出即将插入的数据的主键所在行，对应的count的值，然后赋值给count
 */
object Function2Boot extends BaseApp {

  def main(args: Array[String]): Unit = {
    runApp {

      //有状态计算，设置检查点
      streamingContext.checkpoint("function2")

      //从kafka中读取数据，封装为AdsInfo(1595575286651,华南,深圳,103,1,2020-07-24,15:21)
      val ds: DStream[AdsInfo] = getAllBeans(getDataFromKafka())

      //DStream中有许多批数据，每批数据都有很多AdsInfo对象，每批底层可以看成一个Rdd
      // RDD1 :  ((2020-07-30,华南,深圳,1) ,5)      RDD2 :  ((2020-07-30,华南,深圳,1) ,10)
      val result: DStream[((String, String, String, String), Int)] = ds.map(adsInfo => ((adsInfo.dayString, adsInfo.area, adsInfo.city, adsInfo.adsId), 1))
        .updateStateByKey((seq: Seq[Int], opt: Option[Int]) => Some(seq.sum + opt.getOrElse(0)))

      //将结果写入Mysql
      result.foreachRDD(rdd => {
        rdd.foreachPartition(iter => {

          //一个分区作为一个整体，一个分区只需要创建一个Connection对象
          //创建连接
          val connection = JDBCUtil.getConnection()

          //准备sql
          var sql =
            """
              |INSERT INTO `area_city_ad_count` VALUES(?,?,?,?,?)
              |ON DUPLICATE KEY UPDATE COUNT = ?
              |""".stripMargin

          //获取预处理Statement
          val ps = connection.prepareStatement(sql)

          //给占位符赋值
          iter.foreach {
            case ((day, area, city, ads), count) =>
              ps.setString(1, day)
              ps.setString(2, area)
              ps.setString(3, city)
              ps.setString(4, ads)
              ps.setInt(5, count)
              ps.setInt(6, count)
              ps.executeUpdate()
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
