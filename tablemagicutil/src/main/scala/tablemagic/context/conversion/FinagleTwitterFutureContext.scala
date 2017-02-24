package tablemagic.context.conversion

import com.twitter.util.Future
import io.getquill.context.sql.SqlContext

trait FinagleTwitterFutureContext { self: SqlContext[_, _] =>

  import scala.language.experimental.macros

  def genericRun[T](quoted: Quoted[Query[T]]): Future[List[T]] =
    macro FinagleTwitterFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[T]): Future[T] =
    macro FinagleTwitterFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[Action[T]]): Future[Long] =
    macro FinagleTwitterFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[ActionReturning[_, T]]): Future[T] =
    macro FinagleTwitterFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[BatchAction[Action[T]]]): Future[List[Long]] =
    macro FinagleTwitterFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[BatchAction[ActionReturning[_, T]]]): Future[List[T]] =
    macro FinagleTwitterFutureMacro.impl[T]

}
