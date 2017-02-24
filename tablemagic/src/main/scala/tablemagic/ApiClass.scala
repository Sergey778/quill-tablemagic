package tablemagic

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.meta._
import scala.collection.immutable.Seq

class AutoGenerated extends StaticAnnotation
class RequestIgnore extends StaticAnnotation
class ResponseIgnore extends StaticAnnotation

class ApiClass(requestClassName: String, responseClassName: String) extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    defn match {
      case e @ q"..$mods class $className(..$params) extends ..$parents { ..$defs }" =>
        val (requestName, responseName) = this match {
          case q"new $_(${Lit(req: String)}, ${Lit(resp: String)})" => (req, resp)
          case _ => (s"${className.value}Request", s"${className.value}Response")
        }
        val newDefs = Seq (
          ApiClassHelper.createClass(requestName, ApiClassHelper.getRequestParams(params)),
          ApiClassHelper.createClass(responseName, ApiClassHelper.getResponseParams(params))
        )
        q"""
           ..$mods class $className(..$params) extends ..$parents { ..$defs }
           object ${Term.Name(className.value)} { ..$newDefs }
          """
      case _ => abort("This annotation can be applied only to case classes")
    }
  }

}

private[tablemagic] object ApiClassHelper {

  def createClass(className: String, stats: Seq[Term.Param]) =
    q"case class ${Type.Name(className)}(..$stats)"

  def getCreateRequestParams(stats: Seq[Term.Param]) = filterStats(stats, "AutoGenerated")

  def getRequestParams(stats: Seq[Term.Param]) = filterStats(stats, "RequestIgnore")

  def getResponseParams(stats: Seq[Term.Param]) = filterStats(stats, "ResponseIgnore")

  def filterStats(params: Seq[Term.Param],
                  annotName: String
                 ): Seq[Term.Param] = params.foldLeft(Seq[Term.Param]()) { (result, current) =>
    current match {
      case param"..$mods $paramName: $paramType" if mods.nonEmpty =>
        mods find {
          case Mod.Annot(Term.ApplyUnary(Ctor.Ref.Name(x))) if x == annotName => true
          case _ => false
        } map { _ => result } getOrElse current +: result
      case _ => current +: result
    }
  }.reverse

  private def join[A, B](outer: Traversable[A], inner: Traversable[B])
                      (f: (A, B) => Boolean): Traversable[(A, B)] = {
    for(o <- outer; i <- inner; if f(o, i)) yield (o, i)
  }
}