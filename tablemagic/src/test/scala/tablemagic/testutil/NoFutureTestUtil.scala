package tablemagic.testutil

trait NoFutureTestUtil extends CoreTestUtil {

  protected final def checkResult(result: List[Long], expectedRowChange: Long = 1): Boolean =
    result.forall(p => batchSuccesses(expectedRowChange).contains(p))

  protected final def checkSingleResult(result: Long, expectedRowChange: Long = 1): Boolean =
    batchSuccesses(expectedRowChange).contains(result)

}
