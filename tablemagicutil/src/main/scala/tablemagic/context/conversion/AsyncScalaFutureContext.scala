package tablemagic.context.conversion

import io.getquill.context.sql.SqlContext

import scala.concurrent.{ExecutionContext, Future}

trait AsyncScalaFutureContext { self: SqlContext[_, _] =>

  import scala.language.experimental.macros

  def genericRun[T](quoted: Quoted[Query[T]]): Future[List[T]] =
    macro AsyncScalaFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[T]): Future[T] =
    macro AsyncScalaFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[Action[T]]): Future[Long] =
    macro AsyncScalaFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[ActionReturning[_, T]]): Future[T] =
    macro AsyncScalaFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[BatchAction[Action[T]]]): Future[List[Long]] =
    macro AsyncScalaFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[BatchAction[ActionReturning[_, T]]]): Future[List[T]] =
    macro AsyncScalaFutureMacro.impl[T]

}
