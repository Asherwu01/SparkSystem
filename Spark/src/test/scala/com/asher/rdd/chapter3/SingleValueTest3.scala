package com.asher.rdd.chapter3

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

class SingleValueTest3 {

  /*
     pipe :  允许使用shell脚本处理RDD中的数据！
             RDD调用Pipe时，每一个分区都会被 shell脚本进行处理！
  */

  /*
     sortBy: 全排序
     def sortBy[K](
       f: (T) => K,
       ascending: Boolean = true,
       numPartitions: Int = this.partitions.length)
       (implicit ord: Ordering[K], ctag: ClassTag[K]): RDD[T]
  */
  @Test
  def test5(): Unit = {
    val list = List(1,2,3,9,1,2,7,8)
    val rdd1 = sc.makeRDD(list, 2)
    val rdd2 = rdd1.sortBy(_ + 1)
    rdd2.saveAsTextFile("output")
  }

  /*  coalesce: 合并
            多分区  =>   少分区  ，没有shuffle
            少分区 => 多分区   ， 有shuffle
            coalesce 默认 不支持 由少变多！  由少变多，依然保持少的分区不变！
               可以传入shuffle = true，此时就会产生shuffle！重新分区！
          总结：  ①coalesce默认由多的分区，合并到少的分区，不会产生shuffle
                 ②如果默认由少的分区，合并到多的分区，拒绝，还使用少的分区
                 ③由少的分区，合并到多的分区，开启shuffle=true
          def coalesce(numPartitions: Int, shuffle: Boolean = false,
               partitionCoalescer: Option[PartitionCoalescer] = Option.empty)
              (implicit ord: Ordering[T] = null): RDD[T]

      窄依赖：  当一个父分区，经过转换，产生一个子分区，此时称子分区窄依赖父分区！
                    独生子女
      宽依赖：   当一个父分区，经过转换，产生多个子分区，此时称子分区宽依赖父分区！
                  Wide or shuffle Dependencies
       我想要扩大分区，怎么办？
          shuffle = true
   */
  @Test
  def test4(): Unit = {
    val list = List(1,2,3,9,1,2,7,8)
    val rdd1 = sc.makeRDD(list, 2)
    val rdd2 = rdd1.coalesce(4, true)

    rdd2.saveAsTextFile("output")
  }

  /*
        不使用distinct去重
   */
  @Test
  def test3(): Unit = {
    val list = List(1, 2, 3, 1, 3, 4, 5)
    val rdd1 = sc.makeRDD(list, 1)
    /*val rdd2 = rdd1.map(x => x -> null)
    val rdd3 = rdd2.groupByKey(1)
    val rdd4 = rdd3.map(x => x._1)
    rdd4.saveAsTextFile("output")*/

    val rdd2 = rdd1.groupBy(x => x)
    val rdd3 = rdd2.map(_._1)
    rdd3.saveAsTextFile("output")

  }

  /*
        distinct: 有shuffle,去重后不会改变分区数，可以手动改变
   */
  @Test
  def test2(): Unit = {
    val list = List(1, 2, 3, 4, 5, 3, 21, 3, 7)
    val rdd1 = sc.makeRDD(list, 2)
    val rdd2 = rdd1.distinct(3)
    rdd2.saveAsTextFile("output")

  }

  /*
    sample: 抽样。返回当前RDD的抽样子集！
             withReplacement: Boolean   代表rdd中的元素是否允许被多次抽样！
                   true： 一个元素有可能被多次抽到， 使用PoissonSampler算法抽取！
                   false:  一个元素只能被抽到1次，使用BernoulliSampler算法抽取！

             fraction: 样本比例。   样本大小 /  RDD集合大小  ， 控制 [0,1]  大致！

             seed:  传入一个种子，如果种子，每次抽样的结果一样！
                     如果想获取随机的效果，种子需要真随机！
                     默认就是真随机！

            在一个大型的数据集抽样查询，查看存在数据倾斜！
               某一个RDD中，有大key

            不会改变分区，也没有shuffle!

      def sample(
        withReplacement: Boolean,
        fraction: Double,
        seed: Long = Utils.random.nextLong): RDD[T]
   */
  @Test
  def test1(): Unit = {
    val list = List(1,2,3,4,5,6,7,8)
    val rdd1 = sc.makeRDD(list,2)
    val rdd2 = rdd1.sample(false, 0.5,100)
    rdd2.saveAsTextFile("output")
  }


  val sc: SparkContext = new SparkContext(new SparkConf().setMaster("local").setAppName("MyApp"))
  val path = new Path("output")

  @Before
  def init(): Unit = {
    val fs = FileSystem.get(new Configuration())

    if (fs.exists(path)) {
      fs.delete(path, true)
    }
  }

  @After
  def stop() = {
    sc.stop()
  }
}
