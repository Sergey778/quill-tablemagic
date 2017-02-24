package tablemagic.extension

import java.io.Closeable
import java.util.UUID
import javax.sql.DataSource

import com.typesafe.config.Config
import io.getquill.context.jdbc
import io.getquill.{JdbcContextConfig, NamingStrategy}
import io.getquill.context.jdbc._
import io.getquill.util.LoadConfig

class OracleJdbcContext[N <: NamingStrategy](dataSource: DataSource with Closeable)
  extends JdbcContext[OracleDialect, N](dataSource) with UUIDStringEncoding {

  def this(config: JdbcContextConfig) = this(config.dataSource)
  def this(config: Config) = this(JdbcContextConfig(config))
  def this(configPrefix: String) = this(LoadConfig(configPrefix))
}

