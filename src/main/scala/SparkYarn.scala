import org.apache.spark.{SparkConf, SparkContext}

object SparkYarn {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("sy")
      .setMaster("yarn")
      //      .setMaster("local[*]")
      .set("spark.yarn.archive", "hdfs://node1:9000/sparkjars")
      .setJars(List("file:///D:/sparkTest2/target/sparkTest2-1.0-SNAPSHOT-jar-with-dependencies.jar"))
      .setIfMissing("spark.driver.host", "192.168.242.1")

    val sc = new SparkContext(conf)

    val d = sc.textFile("hdfs://node1:9000/3.txt")
    //    val d = sc.textFile("file:///D:/sparkTest2/src/main/1.txt")
    d.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).sortByKey()
      .collect
      .foreach(println)

    sc.stop()
  }
}
