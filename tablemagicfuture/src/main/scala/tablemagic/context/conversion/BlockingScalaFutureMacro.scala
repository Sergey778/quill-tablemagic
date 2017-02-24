package tablemagic.context.conversion

import scala.reflect.macros.whitebox

private[conversion] class BlockingScalaFutureMacro(val c: whitebox.Context) {
  import c.universe._

  def impl[T : WeakTypeTag](quoted: Tree): Tree =
    q"scala.concurrent.Future.successful(${c.prefix}.run($quoted))"
}

