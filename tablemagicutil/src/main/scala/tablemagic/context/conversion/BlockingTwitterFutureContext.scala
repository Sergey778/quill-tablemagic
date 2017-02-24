package tablemagic.context.conversion

import com.twitter.util.Future
import io.getquill.context.sql.SqlContext

trait BlockingTwitterFutureContext { self: SqlContext[_, _] =>

  import scala.language.experimental.macros

  def genericRun[T](quoted: Quoted[Query[T]]): Future[List[T]] =
    macro BlockingTwitterFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[T]): Future[T] =
    macro BlockingTwitterFutureMacro.impl[T]

  def genericRun(quoted: Quoted[Action[_]]): Future[Long] =
    macro BlockingTwitterFutureMacro.impl[Any]

  def genericRun[T](quoted: Quoted[ActionReturning[_, T]]): Future[T] =
    macro BlockingTwitterFutureMacro.impl[T]

  def genericRun(quoted: Quoted[BatchAction[Action[_]]]): Future[List[Long]] =
    macro BlockingTwitterFutureMacro.impl[Any]

  def genericRun[T](quoted: Quoted[BatchAction[ActionReturning[_, T]]]): Future[List[T]] =
    macro BlockingTwitterFutureMacro.impl[T]

}
