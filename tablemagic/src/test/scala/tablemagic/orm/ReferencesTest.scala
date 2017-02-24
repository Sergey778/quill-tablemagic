package tablemagic.orm

import com.twitter.util.Future
import org.scalatest.{FlatSpec, Matchers}
import tablemagic.testutil.TwitterFutureTestUtil

class ReferencesTest extends FlatSpec with Matchers with TwitterFutureTestUtil {

  import tablemagic.db
  import tablemagic.db._

  import AnnotModel._

  "`Table` annotation" should "not interfere execution of insertion in table" in {
    val insertUsers = quote {
      liftQuery(users.toList).foreach { user =>
        query[QuillTestUser].insert(user)
      }
    }

    checkResult(genericRun(insertUsers)) shouldEqual true

    val insertTokens: Seq[Future[Long]] = tokens map { token: QuillTestUserToken =>
      genericRun(query[QuillTestUserToken].insert(lift(token)))
    }

    checkResult(Future.collect(insertTokens).map(_.toList)) shouldEqual true

    val insertA = quote {
      liftQuery(tableA).foreach { a =>
        query[QuillTableA].insert(a)
      }
    }

    checkResult(genericRun(insertA)) shouldEqual true

    val insertB = quote {
      liftQuery(tableB).foreach { b =>
        query[QuillTableB].insert(b)
      }
    }

    checkResult(genericRun(insertB)) shouldEqual true

    val insertC = quote {
      liftQuery(tableC).foreach { c =>
        query[QuillTableC].insert(c)
      }
    }

    checkResult(genericRun(insertC)) shouldEqual true
  }

  "`Table` annotation" should "generate `user` method" in {
    val someToken = tokens.head
    val userId = someToken.userId
    val manualUser = await(genericRun(query[QuillTestUser].filter(_.id == lift(userId)))).head
    val automaticUser = await(someToken.user)

    manualUser shouldEqual automaticUser
  }

  it should "work with `ConnectionType.ZeroOrOne`" in {
    val b = tableB.head
    val aId = b.quillTableAId
    val manual: QuillTableA = await(genericRun(query[QuillTableA].filter(x => lift(aId).contains(x.id)))).head
    val automatic: Option[QuillTableA] = await(b.quillTableA)

    automatic.contains(manual) shouldEqual true
  }

  it should "work with `ConnectionType.Many`" in {
    val c = tableC.head
    val aName = c.quillTableAName
    val manual: List[QuillTableA] = await(genericRun(query[QuillTableA].filter(x => x.name == lift(aName))))
    val automatic: List[QuillTableA] = await(c.quillTableA)

    manual.sortBy(_.id) shouldEqual automatic.sortBy(_.id)
  }

  "`Table` annotation" should "not interfere execution of deletion from table" in {
    val deleteUsers = quote {
      query[QuillTestUser].delete
    }

    val deleteTokens = quote {
      query[QuillTestUserToken].delete
    }

    checkSingleResult(genericRun(deleteTokens), tokens.length) shouldEqual true

    checkSingleResult(genericRun(deleteUsers), users.length) shouldEqual true

    checkSingleResult(genericRun(query[QuillTableC].delete), tableC.length) shouldEqual true

    checkSingleResult(genericRun(query[QuillTableB].delete), tableB.length) shouldEqual true

    checkSingleResult(genericRun(query[QuillTableA].delete), tableA.length) shouldEqual true
  }
}
