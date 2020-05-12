import org.apache.spark.{SparkConf, SparkContext}

object SparkStandalone {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("ss")
      .setMaster("spark://node1:7077")
      //      .setMaster("local[*]")
      .setJars(List("file:///D:/sparkTest2/target/sparkTest2-1.0-SNAPSHOT-jar-with-dependencies.jar"))
      .setIfMissing("spark.driver.host", "192.168.242.1")

    val sc = new SparkContext(conf)

    val d = sc.textFile("hdfs://node1:9000/2.txt")
    //    val d = sc.textFile("file:///D:/sparkTest2/src/main/1.txt")
    d.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).sortByKey()
      .collect
      .foreach(println)

    sc.stop()
  }
}