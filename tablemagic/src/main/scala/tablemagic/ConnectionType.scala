package tablemagic

import tablemagic.ConnectionType.{Many, One, ZeroOrOne}
import tablemagic.context.FutureType
import tablemagic.context.FutureType._

import scala.meta._

sealed trait ConnectionType {

  private[tablemagic] def endQuasiquote(futureType: FutureType): Term = {
    val base = q"genericRun(q)"
    val transformation = this match {
      case ZeroOrOne => q"headOption"
      case One => q"head"
      case Many => q"toList"
    }
    futureType match {
      case NoFuture => q"$base.$transformation"
      case _ => q"$base.map(_.$transformation)"
    }
  }

  private[tablemagic] def filterQuasiquote(appliedFieldName: Term, tableFieldName: Term.Name) = this match {
    case One | Many => q"x => lift($appliedFieldName) == x.$tableFieldName"
    case ZeroOrOne => q"x => lift($appliedFieldName).contains(x.$tableFieldName)"
  }

}

object ConnectionType {

  def fromString(string: String): ConnectionType = string match {
    case "ZeroOrOne" => ZeroOrOne
    case "One" => One
    case "Many" => Many
  }

  def fromTerm(term: Term): ConnectionType = term match {
    case a: Term.Name => fromString(a.value)
    case Term.Select(q, name) => fromString(name.value)
    case _ => abort(s"Can't find ConnectionType from term $term")
  }

  case object ZeroOrOne extends ConnectionType
  case object One extends ConnectionType
  case object Many extends ConnectionType
}
