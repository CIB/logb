import scalaz._
import Scalaz._

object Test {

  // Takes a map whose values are of type Option[B] and yields either None if one
  // of the values was None, or a new map with the extracted values.
  def flattenOptionMap[A, B](input : Map[A, Option[B]]) : Option[Map[A, B]] = {
    input.sequence
  }

  def combineEnvironmentsFoldFunction(left : Option[Map[String, Entity]], right : Option[Map[String, Entity]]) : Option[Map[String, Entity]] = {
    for {
      unpackLeft <- left
      unpackRight <- right
      result <- combineEnvironments(unpackLeft, unpackRight)
    } yield result
  }

  def combineEnvironments(left : Map[String,Entity], right : Map[String,Entity]) : Option[Map[String, Entity]] = {
    val combinedEnvironments = zipMaps(left, right) mapValues {
      case (Some(l), None) => Some(l)
      case (None, Some(r)) => Some(r)
      case _ => None
    }
    combinedEnvironments.sequence
  }

  def zipMaps[K, L, R](left: Map[K, L], right: Map[K, R]) = {
    val mapSeq = for(key <- left.keys ++ right.keys)
      yield (key, (left.get(key), right.get(key)))

    mapSeq.toMap
  }
}
