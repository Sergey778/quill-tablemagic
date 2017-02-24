package tablemagic.testutil

import scala.concurrent.{Await => ScalaAwait, Future => ScalaFuture}
import scala.concurrent.duration.{Duration => ScalaDuration}

import com.twitter.util.{Future => TwitterFuture, Await => TwitterAwait, Duration => TwitterDuration}

trait CoreTestUtil {
  @inline
  protected final def batchSuccesses(expectedRowChange: Long = 1): List[Long] =
    expectedRowChange :: -2.toLong :: Nil

  @inline
  protected final def await[T](f: ScalaFuture[T]): T = ScalaAwait.result(f, ScalaDuration.Inf)

  @inline
  protected final def await[T](f: TwitterFuture[T]): T = TwitterAwait.result(f, TwitterDuration.Top)
}
