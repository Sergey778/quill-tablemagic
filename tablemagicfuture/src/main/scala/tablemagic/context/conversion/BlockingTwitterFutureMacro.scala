package tablemagic.context.conversion

import scala.reflect.macros.whitebox

private[conversion] class BlockingTwitterFutureMacro(val c: whitebox.Context) {
  import c.universe._

  def impl[T : WeakTypeTag](quoted: Tree): Tree =
    q"com.twitter.util.Future { ${c.prefix}.run($quoted) }"
}
