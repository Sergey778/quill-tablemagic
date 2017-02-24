package tablemagic.context.conversion

import io.getquill.context.sql.SqlContext

trait AsyncNoFutureContext { self: SqlContext[_, _] =>

  import scala.language.experimental.macros

  def genericRun[T](quoted: Quoted[Query[T]]): List[T] =
    macro AsyncNoFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[T]): T =
    macro AsyncNoFutureMacro.impl[T]

  def genericRun(quoted: Quoted[Action[_]]): Long =
    macro AsyncNoFutureMacro.impl[Any]

  def genericRun[T](quoted: Quoted[ActionReturning[_, T]]): T =
    macro AsyncNoFutureMacro.impl[T]

  def genericRun(quoted: Quoted[BatchAction[Action[_]]]): List[Long] =
    macro AsyncNoFutureMacro.impl[Any]

  def genericRun[T](quoted: Quoted[BatchAction[ActionReturning[_, T]]]): List[T] =
    macro AsyncNoFutureMacro.impl[T]

}
