package com.asher.spark.tutorial.streaming_project.app


import com.asher.spark.tutorial.streaming_project.bean.AdsInfo
import org.apache.spark.streaming.Minutes
import org.apache.spark.streaming.dstream.DStream

/**
 * Created by VULCAN on 2020/7/24
 *
 * 最近一小时广告点击量
 *    1：List [15:50->10,15:51->25,15:52->30]
 *
 *    定义窗口：  窗口的范围应该为60分钟
 *                  将每个广告，按照 小时：分钟 ，聚合
 *
 *     AdsInfo(1595575286651,华南,深圳,103,1,2020-07-24,15:21) =>  ((15:21,1) ,1)  ((时间，广告),1)
 *
 *      ((15:21,1) ,1) => 聚合的范围应该是一个 1h的窗口   ((15:21,1) ,10) ,((15:22,1) ,11)
 *
 *      ((15:21,1) ,10) ,((15:22,1) ,11) => 按照 广告id分组  (1, List((15:21,10),(15:22,11)) )
 */
object FunctionDemo3 extends BaseApp {

  def main(args: Array[String]): Unit = {

    runApp{

      //从kafka获取数据
      val ds: DStream[AdsInfo] = getAllBeans(getDataFromKafka)

      //定义窗口,完成操作
      val result: DStream[(String, List[(String, Int)])] = ds.window(Minutes(60))
        .map(adsInfo => {
          ((adsInfo.adsId, adsInfo.hmString), 1)
        })
        .reduceByKey(_ + _)
        .map {
          case ((adsId, hmString), count) => (adsId, (hmString, count))
        }
        .groupByKey()
        .mapValues(it => it.toList.sortBy(_._1))


      result.print(100)

    }
  }

}
