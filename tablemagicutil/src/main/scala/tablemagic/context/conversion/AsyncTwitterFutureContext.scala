package tablemagic.context.conversion

import io.getquill.context.sql.SqlContext

import com.twitter.util.Future

trait AsyncTwitterFutureContext { self: SqlContext[_, _] =>

  import scala.language.experimental.macros

  def genericRun[T](quoted: Quoted[Query[T]]): Future[List[T]] =
    macro AsyncTwitterFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[T]): Future[T] =
    macro AsyncTwitterFutureMacro.impl[T]

  def genericRun(quoted: Quoted[Action[_]]): Future[Long] =
    macro AsyncTwitterFutureMacro.impl[Any]

  def genericRun[T](quoted: Quoted[ActionReturning[_, T]]): Future[T] =
    macro AsyncTwitterFutureMacro.impl[T]

  def genericRun(quoted: Quoted[BatchAction[Action[_]]]): Future[List[Long]] =
    macro AsyncTwitterFutureMacro.impl[Any]

  def genericRun[T](quoted: Quoted[BatchAction[ActionReturning[_, T]]]): Future[List[T]] =
    macro AsyncTwitterFutureMacro.impl[T]

}
