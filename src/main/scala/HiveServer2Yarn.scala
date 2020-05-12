import java.sql.{Connection, DriverManager, ResultSet}

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object HiveServer2Yarn {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName)
      //      .setMaster("local[*]")
      .setMaster("yarn")
    val session = SparkSession.builder()
      .config(conf)
      .config("spark.yarn.archive", "hdfs://node1:9000/sparkjars")
      .getOrCreate()

    val url = "jdbc:hive2://node1:10000/default"
    val username = ""
    val password = ""

    val driverName = "org.apache.hive.jdbc.HiveDriver"
    try {
      Class.forName(driverName)
    } catch {
      case e: ClassNotFoundException =>
        println("Missing Class", e)
    }
    val con: Connection = DriverManager.getConnection(url, username, password)
    val state = con.createStatement()
    val sql = "select * from test"
    val sql1 = "select f3,count(*) from test group by f3"

    println("运行...." + sql1)

    val set: ResultSet = state.executeQuery(sql1)

    while (set.next()) {
      for (i <- 1 to set.getMetaData.getColumnCount) {
        if (set.getObject(i) != null) {
          println(set.getObject(i))
        }
      }
    }
  }
}
