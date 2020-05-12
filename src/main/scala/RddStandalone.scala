import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RddStandalone {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName(this.getClass.getSimpleName)
      //      .setMaster("local[*]")
      .setMaster("spark://node1:7077")
      .setJars(List("file:///D:/sparkTest2/target/sparkTest2-1.0-SNAPSHOT-jar-with-dependencies.jar"))
      .setIfMissing("spark.driver.host", "192.168.242.1")

    val sc: SparkContext = new SparkContext(conf)

    val d: RDD[String] = sc.textFile("hdfs://node1:9000/2.txt")
    //    val d = sc.textFile("file:///D:/sparkTest2/src/main/1.txt")
    d.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).sortByKey()
      .collect
      .foreach(println)

    sc.stop()
  }
}