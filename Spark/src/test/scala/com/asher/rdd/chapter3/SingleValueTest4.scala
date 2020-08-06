package com.asher.rdd.chapter3
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

//自定义Person，用于比较
case class Person1(name:String,age:Int)
case class Person2(name:String,age:Int) extends Ordered[Person2] {
  override def compare(that: Person2): Int = this.age - that.age

}
class SingleValueTest4 {


  /*
      sortBy() 对自定义对象进行排序
      def sortBy[K](
          f: (T) => K,
          ascending: Boolean = true,
          numPartitions: Int = this.partitions.length)
          (implicit ord: Ordering[K], ctag: ClassTag[K]): RDD[T] = withScope {
             this.keyBy[K](f)
            .sortByKey(ascending, numPartitions)
            .values
          }
        ascending指定升序还是降序，默认true，升序


      scala中提供了Ordering、Ordered
        Ordering : extends Comparator[T]
                    通过一个比较器实例，调用比较器对象的compare()方法
        Ordered :  with java.lang.Comparable[A]
                    当前类可排序，比较的时候直接调用当前类的compareTo()方法
   */
  @Test
  def test1(): Unit = {
    val list = List(Person2("asher", 18), Person2("tom", 30),Person2("tom", 19), Person2("jack", 10))
    val rdd1 = sc.makeRDD(list, 2)
    val rdd2 = rdd1.sortBy(p => p,true)
    rdd2.saveAsTextFile("output")
  }



  val sc: SparkContext = new SparkContext(new SparkConf().setMaster("local").setAppName("MyApp"))
  val path = new Path("output")
  @Before
  def init():Unit = {
    val fs = FileSystem.get(new Configuration())

    if (fs.exists(path)){
      fs.delete(path,true)
    }
  }

  @After
  def stop() = {
    sc.stop()
  }
}
