package tablemagic.extension

import io.getquill.NamingStrategy
import io.getquill.ast._
import io.getquill.context.sql.idiom.{OffsetWithoutLimitWorkaround, QuestionMarkBindVariables, SqlIdiom}
import io.getquill.idiom.StatementInterpolator.Tokenizer
import io.getquill.idiom.StatementInterpolator._

trait OracleDialect
  extends SqlIdiom
  with OffsetWithoutLimitWorkaround
  with QuestionMarkBindVariables {
  override def emptyQuery: String = "SELECT 1 FROM DUAL LIMIT 0"

  override implicit def operationTokenizer(implicit propertyTokenizer: Tokenizer[Property],
                                           strategy: NamingStrategy): Tokenizer[Operation] = Tokenizer[Operation] {
    case BinaryOperation(a, NumericOperator.%, b) => stmt"MOD(${a.token}, ${b.token})"
    case x => super.operationTokenizer.token(x)
  }

  override def prepareForProbing(string: String): String = ""
}

object OracleDialect extends OracleDialect
