package tablemagic

import com.typesafe.config.ConfigFactory
import tablemagic.context.{DatabaseType, FutureType}

import scala.annotation.StaticAnnotation
import scala.meta._
import scala.collection.immutable.Seq
import scala.meta.Defn.Def
import scala.meta.Importee.Wildcard

class Table(
             ctxTypeName: String = TableHelper.defaultContextName,
             databaseType: DatabaseType = DatabaseType.fromConfig(),
             futureType: FutureType = FutureType.fromConfig()
           ) extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    lazy val config = ConfigFactory.load(getClass.getClassLoader)

    val (context, database, future) = this match {
      case q"new $_(${Lit(ctxTypeName: String)}, ${Term.Name(databaseTypeName)}, ${Term.Name(futureTypeName)})" =>
        (ctxTypeName, DatabaseType.fromString(databaseTypeName), FutureType.fromString(futureTypeName))
      case q"new $_(${Lit(ctxTypeName: String)})" =>
        (ctxTypeName, DatabaseType.fromConfig(config), FutureType.fromConfig(config))
      case _ => (TableHelper.defaultContextName, DatabaseType.fromConfig(config), FutureType.fromConfig(config))
    }

    defn match {
      case q"..$mods class $className(..$params) extends ..$parents { ..$defs }" =>
        val newDefs = new TableHelper(context, database, future).extendParams(params) ++ defs
        q"..$mods class $className(..$params) extends ..$parents { ..$newDefs }"
      case _ =>
        abort("This annotation can be applied only to case classes")
    }
  }

}


private[tablemagic] class TableHelper(contextTypeName: String, databaseType: DatabaseType, futureType: FutureType) {

  def extendParams(params: Seq[Term.Param]): Seq[Def] = params flatMap {
    case param"..$mods $paramName: $paramType" if mods.nonEmpty =>
      // Find Annotation 'References' and get type parameters and parameters from this
      // abort(mods.map(_.structure).mkString)

      val parameters = mods.foldLeft((Option.empty[Type.Name], Seq[Term.Arg]())) {
        case (result, Mod.Annot(Term.Apply(
        Term.ApplyType(Ctor.Ref.Name("References"), (referencingTableType: Type.Name) :: Nil),
        referencesArgs))) =>
          Some(referencingTableType) -> replaceEmpties(referencesArgs, paramName.value, referencingTableType)
        case (result, Mod.Annot(Term.ApplyType(Ctor.Ref.Name("References"), Seq(referencingTableType: Type.Name, _*)))) =>
          Some(referencingTableType) -> replaceEmpties(Seq(), paramName.value, referencingTableType)
        case (result, _) => result
      }

      parameters match {
        case (Some(tableType),
          (ct: Term) :: Lit(fname: String) :: Lit(tfname: String) :: Nil) =>
          val connectionType = ConnectionType.fromTerm(ct)
          val fieldName = Term.Name(fname)
          val tableFieldName = Term.Name(tfname)
          Some(generateDef(tableType, connectionType, Term.Name(paramName.value), fieldName, tableFieldName))
        case _ => None
      }
    case _ => None
  }

  def generateDef(tableType: Type.Name,
                  connectionType: ConnectionType,
                  appliedFieldName: Term,
                  fieldName: Term.Name,
                  tableFieldName: Term.Name): Def = {
    val importStatement = Import(Seq(Importer(Term.Name(contextTypeName), Seq(Wildcard()))))
    //abort(importStatement.syntax)
    q"""
       def $fieldName = {
        $importStatement
        import tablemagic._
        implicit val schema = schemaMeta[$tableType](${Lit(tableType.value)})
        val q = quote {
          query[$tableType].filter(${connectionType.filterQuasiquote(appliedFieldName, tableFieldName)})
        }
        ${connectionType.endQuasiquote(futureType)}
       }
     """
  }


  def replaceEmpties(args: Seq[Term.Arg], appliedFieldName: String, tableType: Type.Name): Seq[Term.Arg] = {
    import TableHelper._
    val defaultConnectionType = Term.Select(Term.Name("ConnectionType"), Term.Name("One"))
    val defaultFieldName = Lit(appliedFieldName.take(tableType.value.length).lowerFirstLetter)
    val defaultTableFieldName = Lit(appliedFieldName.drop(tableType.value.length).toLowerCase)
    val defaultArgsList =
      defaultConnectionType :: defaultFieldName :: defaultTableFieldName :: Nil
    args ++: defaultArgsList.takeRight(defaultArgsList.length - args.length)
  }
}

private object TableHelper {
  def packageName: String = getClass.getPackage.getName

  def defaultContextName: String = s"$packageName.db"

  implicit class StringExtension(val s: String) extends AnyVal {
    def lowerFirstLetter: String = s.head.toLower + s.tail
  }
}