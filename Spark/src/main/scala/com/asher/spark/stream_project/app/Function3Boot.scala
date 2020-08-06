package com.asher.spark.stream_project.app

import com.asher.spark.stream_project.app.Function2Boot.{getAllBeans, getDataFromKafka}
import com.asher.spark.stream_project.bean.AdsInfo
import org.apache.spark.streaming.Minutes
import org.apache.spark.streaming.dstream.DStream

/**
 *    需求三：
 *        最近一小时广告点击量
 *
 *    结果样例：
 *        1：List [15:50->10,15:51->25,15:52->30]
 *
 *    分析：
 *        定义窗口：  窗口的范围应该为60分钟
 *                  将每个广告，按照  (广告 小时：分钟) ，聚合
 *
 *      AdsInfo(1595575286651,华南,深圳,103,1,2020-07-24,15:21) =>  ((1,15:21) ,1)  ((广告，时间),1)
 *
 *     ((1,15:21) ,1) => 聚合的范围应该是一个 1h的窗口   ((1,15:21) ,10) ,((1,15:22) ,20)
 *
 *      (((1,15:21) ,10) ,((1,15:22) ,20) => 按照 广告id分组  (1, List((15:21,10),(15:22,20)) )
 *
 */
object Function3Boot extends BaseApp {

  def main(args: Array[String]): Unit = {
    runApp{

      //从kafka中读取数据，封装为AdsInfo(1595575286651,华南,深圳,103,1,2020-07-24,15:21)
      val ds: DStream[AdsInfo] = getAllBeans(getDataFromKafka())

      //定义窗口，窗口大小1h
      val result = ds.window(Minutes(60))
        .map(adsInfo => {
          ((adsInfo.adsId, (adsInfo.dayString + ":" + adsInfo.hmString)), 1)
        })
        .reduceByKey(_ + _)
        .map {
          case ((adsId, hm), count) =>
            (adsId, (hm, count))
        }
        .groupByKey()

      result.print(100)
    }
  }
}
