package tablemagic.context.conversion

import io.getquill.context.sql.SqlContext

import scala.concurrent.Future

trait FinagleScalaFutureContext { self: SqlContext[_, _] =>

  import scala.language.experimental.macros

  def genericRun[T](quoted: Quoted[Query[T]]): Future[List[T]] =
    macro FinagleScalaFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[T]): Future[T] =
    macro FinagleScalaFutureMacro.impl[T]

  def genericRun(quoted: Quoted[Action[_]]): Future[Long] =
    macro FinagleScalaFutureMacro.impl[Nothing]

  def genericRun[T](quoted: Quoted[ActionReturning[_, T]]): Future[T] =
    macro FinagleScalaFutureMacro.impl[T]

  def genericRun(quoted: Quoted[BatchAction[Action[_]]]): Future[Long] =
    macro FinagleScalaFutureMacro.impl[Nothing]

  def genericRun[T](quoted: Quoted[BatchAction[ActionReturning[_, T]]]): Future[List[T]] =
    macro FinagleScalaFutureMacro.impl[T]

}
