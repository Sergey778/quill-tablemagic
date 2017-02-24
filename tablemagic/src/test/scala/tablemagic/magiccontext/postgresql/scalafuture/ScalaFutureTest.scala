package tablemagic.magiccontext.postgresql.scalafuture

import java.util.UUID

import io.getquill.{Escape, SnakeCase}
import org.scalatest.{FlatSpec, Matchers}
import tablemagic.context.{DatabaseType, FutureType, MagicContext}
import tablemagic.testutil.ScalaFutureTestUtil

class ScalaFutureTest extends FlatSpec with Matchers with ScalaFutureTestUtil {

  object Context {
    @MagicContext(DatabaseType.AsyncPostgres, FutureType.ScalaFuture)
    class PGAsyncContext

    implicit val pgasync = new PGAsyncContext[SnakeCase with Escape]("pgasync")
  }

  import Context._
  import tablemagic.SimpleModel._

  import pgasync._
  "PostgresAsync Context" should "be able to batch insert data into table" in {

    val insertQuery = quote {
      liftQuery(users.toList).foreach(user => query[QuillTestUser].insert(user))
    }

    checkResult(genericRun(insertQuery)) shouldEqual true
  }

  it should "be able to insert data into table" in {

    for (token <- tokens) {
      checkSingleResult(genericRun(query[QuillTestUserToken].insert(lift(token)))) shouldEqual true
    }
  }

  it should "be able to get all data from table" in {
    val allQuery = quote {
      query[QuillTestUser]
    }

    val quotedUsers: List[QuillTestUser] = await(genericRun(allQuery))
    val inlinedUsers: List[QuillTestUser] = await(genericRun(query[QuillTestUser]))

    users forall { user =>
      quotedUsers.contains(user) && inlinedUsers.contains(user)
    } shouldEqual true
  }

  it should "be able to get filtered data from table" in {
    val filteredQuery = quote {
      query[QuillTestUser].filter(x => x.id < 3)
    }

    val quotedUsers: List[QuillTestUser] = await(genericRun(filteredQuery))

    users.filter(_.id < 3) foreach { user =>
      quotedUsers.contains(user) shouldEqual true
    }
  }

  it should "be able to partially update data in table" in {
    val updateQuery = quote {
      query[QuillTestUserToken].filter(x => x.userId == 0).update(_.userId -> 2)
    }

    checkSingleResult(genericRun(updateQuery), tokens.count(_.userId == 0)) shouldEqual true

    val checkQuery = quote {
      query[QuillTestUserToken].filter(_.userId == 0).size
    }

    await(genericRun(checkQuery)) shouldEqual 0
  }

  it should "be able to fully update data in table" in {
    def newToken = QuillTestUserToken(UUID.randomUUID(), 1)
    val updateQuery = quote {
      query[QuillTestUserToken].filter(_.userId == 1).update(lift(newToken))
    }

    checkSingleResult(genericRun(updateQuery), tokens.count(_.userId == 1)) shouldEqual true

    await(genericRun(query[QuillTestUserToken].filter(_.userId == 1).size)) shouldEqual 1
  }

  it should "be able to delete data from table" in {
    val deleteQuery = quote {
      query[QuillTestUserToken].delete
    }

    checkSingleResult(genericRun(deleteQuery), tokens.length) shouldEqual true
  }

  it should "be able to batch delete data from table" in {
    val deleteQuery = quote {
      liftQuery(users.toList).foreach(user => query[QuillTestUser].filter(_.id == user.id).delete)
    }

    checkResult(genericRun(deleteQuery)) shouldEqual true
  }

}
