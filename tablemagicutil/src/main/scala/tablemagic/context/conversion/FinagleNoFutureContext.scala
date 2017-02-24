package tablemagic.context.conversion

import com.twitter.util.Future
import io.getquill.context.sql.SqlContext

trait FinagleNoFutureContext { self: SqlContext[_, _] =>

  import scala.language.experimental.macros

  def genericRun[T](quoted: Quoted[Query[T]]): List[T] =
    macro FinagleNoFutureMacro.impl[T]

  def genericRun[T](quoted: Quoted[T]): T =
    macro FinagleNoFutureMacro.impl[T]

  def genericRun(quoted: Quoted[Action[_]]): Long =
    macro FinagleNoFutureMacro.impl[Any]

  def genericRun[T](quoted: Quoted[ActionReturning[_, T]]): T =
    macro FinagleNoFutureMacro.impl[T]

  def genericRun(quoted: Quoted[BatchAction[Action[_]]]): List[Long] =
    macro FinagleNoFutureMacro.impl[Any]

  def genericRun[T](quoted: Quoted[BatchAction[ActionReturning[_, T]]]): List[T] =
    macro FinagleNoFutureMacro.impl[T]

}
