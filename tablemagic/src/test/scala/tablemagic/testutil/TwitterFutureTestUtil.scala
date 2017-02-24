package tablemagic.testutil

import com.twitter.util.Future

trait TwitterFutureTestUtil extends CoreTestUtil {

  protected def checkResult(result: Future[List[Long]], expectedRowChange: Long = 1) = await {
    result.map(_.forall(p => batchSuccesses(expectedRowChange).contains(p)))
  }

  protected def checkSingleResult(result: Future[Long], expectedRowChange: Long = 1) =
    batchSuccesses(expectedRowChange).contains(await(result))

}
