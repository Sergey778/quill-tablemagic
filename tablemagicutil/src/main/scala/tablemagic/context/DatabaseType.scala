package tablemagic.context

import com.typesafe.config.{Config, ConfigFactory}
import tablemagic.context.DatabaseType._
import tablemagic.context.FutureType._

import scala.meta._
import scala.util.Try

sealed trait DatabaseType {

  def defaultFutureType: FutureType = this match {
    case _: BlockingDatabaseType => NoFuture
    case _: AsyncDatabaseType => ScalaFuture
    case _: FinagleDatabaseType => TwitterFuture
  }

  private[context] def prefix = this match {
    case _: BlockingDatabaseType => s"Blocking"
    case _: AsyncDatabaseType => s"Async"
    case _: FinagleDatabaseType => s"Finagle"
  }

  private[context] def extensionTerm = {
    val contextName = extensionString
    if (contextName.contains(".")) Ctor.Ref.Name(contextName)
    else Ctor.Ref.Name(s"io.getquill.$contextName")
  }

  private[context] def extensionString = this match {
    case Postgres => "PostgresJdbcContext"
    case AsyncPostgres => "PostgresAsyncContext"
    case FinaglePostgres => "FinaglePostgresContext"
    case Oracle => s"${packageName.substring(0, packageName.lastIndexOf('.') + 1)}extension.OracleJdbcContext"
  }

  private[context] def packageName = getClass.getPackage.getName

  private[context] def param: Term.Param = param"name: String"

  private[tablemagic] def contextName(futureType: FutureType) =
    s"$prefix${futureType.toString}Context"

}

sealed trait BlockingDatabaseType extends DatabaseType
sealed trait AsyncDatabaseType extends DatabaseType
sealed trait FinagleDatabaseType extends DatabaseType

object DatabaseType {

  def fromString(database: String, default: DatabaseType = Postgres): DatabaseType =
    fromStringOption(database).getOrElse(default)

  def fromStringOption(database: String): Option[DatabaseType] =
    database match {
      case "postgres" | "Postgres" => Some(Postgres)
      case "async-postgres" | "AsyncPostgres" => Some(AsyncPostgres)
      case "finagle-postgres" | "FinaglePostgres" => Some(FinaglePostgres)
      case "oracle" | "Oracle" => Some(Oracle)
      case _ => None
    }

  def fromConfig(config: Config = ConfigFactory.load(),
                 default: DatabaseType = Postgres): DatabaseType =
    Try(config.getString("magictable.database"))
      .map(fromString(_, default))
      .getOrElse(default)

  def fromConfigOption(config: Config = ConfigFactory.load()): Option[DatabaseType] =
    Try(config.getString("magictable.database")).toOption.flatMap(fromStringOption)

  case object Postgres extends BlockingDatabaseType
  case object AsyncPostgres extends AsyncDatabaseType
  case object FinaglePostgres extends FinagleDatabaseType

  case object Oracle extends BlockingDatabaseType
}
