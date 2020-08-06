package com.asher.streaming.chapter9

/**
 *
 * 自定义Receiver:
 * ① 定义onStart() :   完成开始接受数据前的准备工作！
 * ② 定于onStop() :   完成停止接受数据前的清理工作！
 * ③ 异常时，可以调用restart()，重启接收器或调用stop()，彻底停止！
 * ④ 调用store()存储收到的数据
 *
 * 注意： onstart()不能阻塞，收数据需要在新线程中接接收！
 * 异常时，在任意线程都可以调用停止，重启，报错等方法！
 */

import java.io.{BufferedReader, InputStreamReader}
import java.net.{ConnectException, Socket}

import com.asher.tutorial.day09.MyReciver
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.receiver.Receiver
import org.junit._

class CustomReceiver {
  /*
      测试 MyReceiver
   */
  @Test
  def test(): Unit = {
    //创建StreamSpark
    val conf = new SparkConf().setMaster("local[*]").setAppName("MyReceiver")
    val ssc = new StreamingContext(conf, Seconds(2))

    //获取DStream
    val dS1 = ssc.receiverStream(new MyReciver("hadoop102", 9999))
    val dS2 = dS1.flatMap(_.split(" ")).map(_ -> 1).reduceByKey(_ + _)

    dS2.print(100)

    ssc.start()

    ssc.awaitTermination()
  }
}

/*
    自定义接收器，接收 监听端口的信息，实现WordCount
 */
class MyCluster(host: String, port: Int) extends Receiver[String](StorageLevel.MEMORY_ONLY) {
  var socket: Socket = _
  var reader: BufferedReader = _

  def receiveData(): Unit = {
    new Thread {
      //设置成守护线程
      this.setDaemon(true)

      //线程任务，收数据
      override def run(): Unit = {
        try {
          var line = reader.readLine()
          while (socket.isConnected && line != null) {
            store(line)
            //继续读这批数据
            line = reader.readLine()
          }
        } finally {
          onStop()
          restart("已断开，尝试重新连接")
        }
      }

    }.start()
  }

  //完成接收数据前的准备工作
  override def onStart(): Unit = {
    //准备工作
    try {
      socket = new Socket(host,port)
    } catch {
      case e: ConnectException =>
        restart("尝试重新连接")
        return
    } finally {
      onStop()
    }

    //执行到此处，表示连接成功
    println("连接成功")

    reader = new BufferedReader(new InputStreamReader(socket.getInputStream))

    //收数据，onstart()不能阻塞，收数据需要在新线程中接接收，故需要在方法中新开线程
    receiveData()
  }

  //完成停止接收数据前的清理工作
  override def onStop(): Unit = {
    if (socket != null) {
      socket.close()
      socket = null
    }

    if (reader != null) {
      reader.close()
      reader = null
    }
  }
}