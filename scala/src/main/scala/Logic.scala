import scalaz._
import Scalaz._

abstract class Entity {
  def getFreeVariables : Set[String]
  def substituteFreeVariables(env : Map[String,Entity]) : Entity
  def matchAgainst(other : Entity) : Option[Map[String,Entity]]
}

case class EStatement(statementTypeC : String, argumentsC : Map[String, Entity]) extends Entity {
  val statementType = statementTypeC
  val arguments = argumentsC

  override def getFreeVariables : Set[String] = {
    val collectedFreeVariables = arguments.values.map(x => x.getFreeVariables)
    collectedFreeVariables.toSet.flatten
  }

  override def substituteFreeVariables(env : Map[String,Entity]) : Entity = {
    val newArguments = arguments.mapValues(entity => entity.substituteFreeVariables(env))
    EStatement(statementType, newArguments)
  }

  override def matchAgainst(other : Entity) : Option[Map[String,Entity]] = {
    other match {
      case EStatement(otherType, otherArgs) =>
        if(otherType == statementType) {
          // Try to match each argument of this statement pattern against an argument in the
          // instance, resulting in a list of environments that all have to be valid together.
          val zippedValues = (arguments.values, otherArgs.values).zipped
          val resultingEnvironments = zippedValues map ((x, y) => x.matchAgainst(y))

          val init : Option[Map[String, Entity]] = Some(Map.empty)
          resultingEnvironments.foldLeft(init) (Logic.combineEnvironmentsFoldFunction)
        } else {
          None
        }
      case _ => None
    }
  }
}

case class EInteger(valueC : Int) extends Entity {
  val value = valueC

  override def getFreeVariables : Set[String] = {
    Set.empty
  }

  override def substituteFreeVariables(env : Map[String,Entity]) : Entity = {
    this
  }

  override def matchAgainst(other : Entity) : Option[Map[String,Entity]] = {
    other match {
      case EInteger(otherValue) => if(value == otherValue) Some(Map.empty) else None
      case _ => None
    }
  }
}

case class EIdentifier(valueC : String) extends Entity {
  val value = valueC

  override def getFreeVariables : Set[String] = {
    Set(value)
  }

  override def substituteFreeVariables(env : Map[String,Entity]) : Entity = {
    env.get(value) match {
      case Some(entity) => entity
      case None => this
    }
  }

  override def matchAgainst(other : Entity) : Option[Map[String,Entity]] = {
    Some(Map(value -> other))
  }
}

object Logic {
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
      case (Some(l), Some(r)) => if(l == r) Some(l) else None
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
