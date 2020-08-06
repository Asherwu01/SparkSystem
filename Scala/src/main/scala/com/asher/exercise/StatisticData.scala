package com.asher.exercise

object StatisticData {
  def main(args: Array[String]): Unit = {
    val list = List(
      ("zhangsan", "河北", "鞋"),
      ("lisi", "河北", "衣服"),
      ("wangwu", "河北", "鞋"),
      ("zhangsan", "河南", "鞋"),
      ("lisi", "河南", "衣服"),
      ("wangwu", "河南", "鞋"),
      ("zhangsan", "河南", "鞋"),
      ("lisi", "河北", "衣服"),
      ("wangwu", "河北", "鞋"),
      ("zhangsan", "河北", "鞋"),
      ("lisi", "河北", "衣服"),
      ("wangwu", "河北", "帽子"),
      ("zhangsan", "河南", "鞋"),
      ("lisi", "河南", "衣服"),
      ("wangwu", "河南", "帽子"),
      ("zhangsan", "河南", "鞋"),
      ("lisi", "河北", "衣服"),
      ("wangwu", "河北", "帽子"),
      ("lisi", "河北", "衣服"),
      ("wangwu", "河北", "电脑"),
      ("zhangsan", "河南", "鞋"),
      ("lisi", "河南", "衣服"),
      ("wangwu", "河南", "电脑"),
      ("zhangsan", "河南", "电脑"),
      ("lisi", "河北", "衣服"),
      ("wangwu", "河北", "帽子")
    )

    /*
        需求：统计每个省份对应的 商品及商品数量
     */
    //1.List( ("wangwu", "河北", "帽子"),)--map-->Map("河北", "帽子")
    val date1 = list.map(kv => (kv._2, kv._3))

    //data1 = List((河北,鞋), (河北,衣服), (河北,鞋))--map-->List((河北,鞋)->List((河北,鞋),(河北,鞋)))
    val data2 = date1.groupBy(kv => kv)

    //data2 = Map((河北,衣服) -> List((河北,衣服), (河北,衣服), (河北,衣服)),
    //            (河南,鞋) -> List((河南,鞋), (河南,鞋))   value转化成  List((河北,衣服)->3,(河南,鞋)->1)
    //List( List((河北,衣服), (河北,衣服), (河北,衣服)) , List((河南,鞋), (河南,鞋)) )
    val data3 = data2.mapValues(list => {
      (list.head, list.size)
    })
    //Map( (河北,衣服) -> ((河北,衣服),6),     (河南,鞋) -> ((河南,鞋),6),    )
    val data4 = data3.toList
    val data5 = data4.map(kv => {
      kv._1._1 -> (kv._1._2 -> kv._2._2)
    })

    // 排序data5= List((河北,(衣服,6)),   (河南,(鞋,6)),   (河南,(电脑,2))  )
    val res = data5.sortBy(kv => kv._2._2)(Ordering.Int.reverse)
    println(res)
  }
}
