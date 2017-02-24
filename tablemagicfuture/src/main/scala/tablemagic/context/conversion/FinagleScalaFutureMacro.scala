package tablemagic.context.conversion

import scala.reflect.macros.whitebox

private[conversion] class FinagleScalaFutureMacro(val c: whitebox.Context) {
  import c.universe._

  def impl[T : WeakTypeTag](quoted: Tree): Tree = ???
}
