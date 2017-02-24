package tablemagic.testutil

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

trait ScalaFutureTestUtil extends CoreTestUtil {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  protected def checkResult(result: Future[List[Long]], expectedRowChange: Long = 1) = await {
    result.map(_.forall(p => batchSuccesses(expectedRowChange).contains(p)))
  }

  protected def checkSingleResult(result: Future[Long], expectedRowChange: Long = 1) =
    batchSuccesses(expectedRowChange).contains(await(result))

}
