import io.getquill._
import tablemagic.context._

package object tablemagic {
  @MagicContext
  class DefaultContext

  implicit lazy val db = new DefaultContext[SnakeCase with Escape]("db")

}
