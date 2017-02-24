package tablemagic.context.conversion

import scala.reflect.macros.whitebox

private[conversion] class FinagleNoFutureMacro(val c: whitebox.Context) {
  import c.universe._

  def impl[T : WeakTypeTag](quoted: Tree): Tree =
    q"com.twitter.util.Await.result(${c.prefix}.run($quoted), com.twitter.util.Duration.Top)"
}
