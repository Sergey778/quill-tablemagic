package tablemagic

import java.time.LocalDateTime
import java.util.UUID


object SimpleModel {
  // Create model with different data types for testing
  case class QuillTestUser(id: Long, name: String, birthDate: LocalDateTime, salary: Option[Int], male: Boolean)

  case class QuillTestUserToken(token: UUID, userId: Long)

  val users = Seq (
    QuillTestUser(0, "John", LocalDateTime.now.minusYears(25), Some(10000), male = true),
    QuillTestUser(1, "Mary", LocalDateTime.now.minusYears(18), Some(10000), male = false),
    QuillTestUser(2, "Frank", LocalDateTime.now.minusYears(9), None, male = true),
    QuillTestUser(3, "Franchesca", LocalDateTime.now.minusYears(86), None, male = false)
  )

  val tokens = Seq (
    QuillTestUserToken(UUID.randomUUID(), 0),
    QuillTestUserToken(UUID.randomUUID(), 0),
    QuillTestUserToken(UUID.randomUUID(), 0),
    QuillTestUserToken(UUID.randomUUID(), 1),
    QuillTestUserToken(UUID.randomUUID(), 2),
    QuillTestUserToken(UUID.randomUUID(), 2)
  )
}
