package tablemagic.orm

import java.time.LocalDateTime
import java.util.UUID

import tablemagic.{ConnectionType, References, Table}

object AnnotModel {
  @Table
  case class QuillTestUser(
      id: Long,
      name: String,
      birthDate: LocalDateTime,
      salary: Option[Int],
      male: Boolean
  )

  @Table
  case class QuillTestUserToken(
      token: UUID,
      @References[QuillTestUser](ConnectionType.One, "user", "id") userId: Long
  )

  @Table
  case class QuillTableA(id: Long, name: String)

  @Table
  case class QuillTableB(
      id: Long,
      @References[QuillTableA](ConnectionType.ZeroOrOne) quillTableAId: Option[Long]
  )

  @Table
  case class QuillTableC(
      id: Long,
      @References[QuillTableA](ConnectionType.Many) quillTableAName: String
  )

  val tableA = List(
    QuillTableA(0, "A"),
    QuillTableA(1, "A"),
    QuillTableA(2, "A"),
    QuillTableA(3, "B")
  )

  val tableB = List(
    QuillTableB(0, Some(0)),
    QuillTableB(1, Some(1)),
    QuillTableB(2, Some(2)),
    QuillTableB(3, None)
  )

  val tableC = List(
    QuillTableC(0, "A")
  )

  val users = Seq(
    QuillTestUser(0, "John", LocalDateTime.now.minusYears(25), Some(10000), male = true),
    QuillTestUser(1, "Mary", LocalDateTime.now.minusYears(18), Some(10000), male = false),
    QuillTestUser(2, "Frank", LocalDateTime.now.minusYears(9), None, male = true),
    QuillTestUser(3, "Franchesca", LocalDateTime.now.minusYears(86), None, male = false)
  )

  val tokens = Seq(
    QuillTestUserToken(UUID.randomUUID(), 0),
    QuillTestUserToken(UUID.randomUUID(), 0),
    QuillTestUserToken(UUID.randomUUID(), 0),
    QuillTestUserToken(UUID.randomUUID(), 1),
    QuillTestUserToken(UUID.randomUUID(), 2),
    QuillTestUserToken(UUID.randomUUID(), 2)
  )

}
