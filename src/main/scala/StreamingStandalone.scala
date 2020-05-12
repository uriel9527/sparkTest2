import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory

object StreamingStandalone {
  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(this.getClass)
    val conf: SparkConf = new SparkConf().setAppName(this.getClass.getSimpleName)
      //      .setMaster("local[*]")
      .setMaster("spark://node1:7077")
      .setJars(List("file:///D:/sparkTest2/target/sparkTest2-1.0-SNAPSHOT-jar-with-dependencies.jar"))
      .setIfMissing("spark.driver.host", "192.168.242.1")


    val streamingContext: StreamingContext = new StreamingContext(conf, Seconds(10))
    streamingContext.checkpoint("./ck")
    val brokerList = "node1:9092"
    //        val brokerList = "tdh12:9092，tdh13:9092，tdh14:9092"
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokerList,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "g999",
      //      "auto.offset.reset" -> "earliest",//有提交的offset走提交的,没提交从头读
      "auto.offset.reset" -> "latest", //有提交的offset走提交的,没提交读新的
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array("test")
    val directStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(
      streamingContext,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )
    val result: DStream[(String, Int)] = directStream
      .map(_.value())
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)

    result.foreachRDD(_.foreachPartition(_.foreach(println(_)))) //executor端
    println("#################################")
    result.foreachRDD(_.collect().foreach(println(_))) //拉回driver端

    streamingContext.start()
    streamingContext.awaitTermination()
  }

}