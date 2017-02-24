package tablemagic.context

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

sealed trait FutureType

object FutureType {

  def fromString(s: String): FutureType = s match {
    case "scala.concurrent.Future" | "ScalaFuture" => ScalaFuture
    case "com.twitter.util.Future" | "TwitterFuture" => TwitterFuture
    case _ => NoFuture
  }

  def fromConfig(config: Config = ConfigFactory.load(), default: FutureType = TwitterFuture): FutureType =
    Try(config.getString("magictable.future"))
      .map(fromString)
      .getOrElse(default)

  def fromConfigOption(config: Config = ConfigFactory.load()): Option[FutureType] =
    Try(config.getString("magictable.future")).toOption.map(fromString)

  case object NoFuture extends FutureType
  case object ScalaFuture extends FutureType
  case object TwitterFuture extends FutureType
}
