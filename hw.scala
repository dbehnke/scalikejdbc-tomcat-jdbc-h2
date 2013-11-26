import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

import org.apache.tomcat.jdbc.pool.DataSource

import scalikejdbc._, SQLInterpolation._

/**
 * tomcat jdbc Connection Pool Factory
 */
object TomcatH2ConnectionPoolFactory extends ConnectionPoolFactory {
  override def apply(url: String, user: String, password: String,
    settings: ConnectionPoolSettings = ConnectionPoolSettings()) = {
    new TomcatH2ConnectionPool(url, user, password, settings)
  }
}

/**
 * tomcat jdbc Connection Pool
 */
class TomcatH2ConnectionPool(
  override val url: String,
  override val user: String,
  password: String,
  override val settings: ConnectionPoolSettings = ConnectionPoolSettings())
  extends ConnectionPool(url, user, password, settings) {

  
  //import org.apache.tomcat.jdbc.pool.PoolProperties

  private[this] val _dataSource = new org.apache.tomcat.jdbc.pool.DataSource()
  _dataSource.setUrl(url)
  _dataSource.setDriverClassName("org.h2.Driver")
  _dataSource.setUsername(user)
  _dataSource.setPassword(password)
  _dataSource.setInitialSize(settings.initialSize)
  _dataSource.setMaxActive(settings.maxSize)
  _dataSource.setMaxWait(settings.connectionTimeoutMillis.toInt)
  //_dataSource.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
  //                "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer")

  override def dataSource: org.apache.tomcat.jdbc.pool.DataSource = _dataSource
  override def borrow(): Connection = dataSource.getConnection()
  override def numActive: Int = _dataSource.getNumActive()
  override def numIdle: Int = _dataSource.getNumIdle()
  override def maxActive: Int = _dataSource.getMaxActive
  override def maxIdle: Int = _dataSource.getMaxIdle
  override def close(): Unit = _dataSource.close()
}

object Hi {
  def main(args: Array[String]) = {
    Class.forName("org.h2.Driver")
    implicit val session = AutoSession
    //ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")

    val settings = ConnectionPoolSettings(
      initialSize = 5,
      maxSize = 20,
      connectionTimeoutMillis = 3000L,
      validationQuery = "select 1 from dual")

    implicit val factory = TomcatH2ConnectionPoolFactory
    ConnectionPool.add('tomcat,"jdbc:h2:mem:hello", "user", "pass", settings)

    NamedDB('tomcat) readOnly { implicit session => {
        val entities: List[Map[String, Any]] = sql"SELECT * FROM INFORMATION_SCHEMA.HELP".map(_.toMap).list.apply()
        entities.foreach(e => {
        println(e("TOPIC"))
      })
      }
    }
    
    //println(entities)
  }
}
