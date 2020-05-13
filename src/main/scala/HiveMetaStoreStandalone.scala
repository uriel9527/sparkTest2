import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object HiveMetaStoreStandalone {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName)
      //      .setMaster("local[*]")
      .setMaster("spark://node1:7077")

    val session = SparkSession.builder().config(conf)
      .config("hive.metastore.uris", "thrift://node1:9083")
      .config("spark.sql.hive.metastore.version", "0.12.0")
      .config("spark.sql.hive.metastore.jars", "maven")
      //      .config("spark.sql.hive.metastore.jars", "D:/hive012jar")
      .config("spark.sql.warehouse.dir", "hdfs://node1:9000/user/hive/warehouse")
      .config("spark.driver.host", "192.168.242.1")
      .enableHiveSupport()
      .getOrCreate()

    val sql1 = "select f1,f2,f3 from test"
    val sql2 = "insert into table test values('G','G','G',77)"
    //    val sql2 = "select f3,count(*) from test group by f3"
    session.sql(sql1).show()
    session.sql(sql2).show()
    session.sql(sql1).show()

    session.stop()
  }
}
