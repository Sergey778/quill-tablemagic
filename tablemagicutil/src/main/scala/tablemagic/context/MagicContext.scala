package tablemagic.context

import java.net.URL

import com.twitter.util.{Await, Future}
import com.typesafe.config.{Config, ConfigFactory}
import io.getquill._
import io.getquill.context.Context
import io.getquill.context.sql.SqlContext

import scala.annotation.StaticAnnotation
import scala.collection.immutable.Seq
import scala.meta._

class MagicContext(
                    databaseType: DatabaseType = DatabaseType.fromConfig(),
                    futureType: FutureType = FutureType.fromConfig()
                  ) extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    import MagicContextHelper._

    //abort(MagicContextHelper.getClass.getClassLoader.toString)

    val (database, future) = this match {
      case q"new $_(${x: Term.Select}, ${y: Term.Select})" =>
        val (databaseTypeName, futureTypeName) = (x.name.value, y.name.value)
        (DatabaseType.fromString(databaseTypeName), FutureType.fromString(futureTypeName))
      case q"new $_(${Term.Name(databaseTypeName)}, ${Term.Name(futureTypeName)})" =>
        (DatabaseType.fromString(databaseTypeName), FutureType.fromString(futureTypeName))
      case _ =>
        (DatabaseType.fromConfigOption(config), FutureType.fromConfigOption(config)) match {
          case (Some(dt), Some(ft)) => (dt, ft)
          case _ => abort(missingConfigurationMessage)
        }
    }

    val genericContext = s"$packageName.conversion.${database.contextName(future)}"
    val nameTerm: Term = Term.Name("name")

    val t: Type.Param = tparam"R <: io.getquill.NamingStrategy"
    val typeName: Type = Type.Name(t.name.value)
    val parent: Ctor.Call = Term.Apply(Term.ApplyType(database.extensionTerm, Seq(typeName)), Seq(nameTerm))

    val async: Ctor.Call = Term.Apply(Ctor.Ref.Name(genericContext), Nil)

    defn match {
      case q"..$mods class $className" =>
        q"..$mods class $className[$t](..${Seq(database.param)}) extends $parent with $async {}"
      case q"..$mods class $className(..$params) { ..$stats }" =>
        val newParams = params :+ database.param
        q"..$mods class $className[$t](..$newParams) extends $parent with $async { ..$stats }"
      case _ =>
        abort("This annotation supports only classes")
    }
  }

}

private object MagicContextHelper {

  lazy val config: Config = ConfigFactory.load(this.getClass.getClassLoader)

  def packageName: String = this.getClass.getPackage.getName

  final val missingConfigurationMessage =
    """
      Can't load configuration to create context.
      Be sure that you're using one of following forms:
      Implicit configuration:
        // Loads configuration from file. Need to "magictable.database" and "magictable.future" fields in configuration
        @MagicContext
        class ExampleContext
      Explicit configuration:
        // Uses explicit configuration. Check out `DatabaseType` and `FutureType`.
        @MagicContext(DatabaseType.AsyncPostgres, FutureType.ScalaFuture)
        class ExampleContext
    """
}
