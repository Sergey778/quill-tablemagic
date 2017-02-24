package tablemagic.context.conversion

import io.getquill.context.sql.SqlContext

trait BlockingNoFutureContext { self: SqlContext[_, _] =>

  import scala.language.experimental.macros

  def genericRun[T](quoted: Quoted[Query[T]]): List[T] =
    macro BlockingNoFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[T]): T =
    macro BlockingNoFutureMacro.impl[T]

  def genericRun(quoted: Quoted[Action[_]]): Long =
    macro BlockingNoFutureMacro.impl[Any]

  def genericRun[T](quoted: Quoted[ActionReturning[_, T]]): T =
    macro BlockingNoFutureMacro.impl[T]

  def genericRun(quoted: Quoted[BatchAction[Action[_]]]): List[Long] =
    macro BlockingNoFutureMacro.impl[Any]

  def genericRun[T](quoted: Quoted[BatchAction[ActionReturning[_, T]]]): List[T] =
    macro BlockingNoFutureMacro.impl[T]

}
