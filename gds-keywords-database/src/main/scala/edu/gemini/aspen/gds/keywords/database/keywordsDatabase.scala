package edu.gemini.aspen.gds.keywords.database

import edu.gemini.aspen.giapi.data.DataLabel
import scala.actors.Actor
import edu.gemini.aspen.gds.api.CollectedValue

/**
  * Interface for the database */
trait KeywordsDatabase extends Actor

//case classes define the messages accepted by the DataBase
sealed trait KeywordsDatabaseAction

//store a CollectedValue associated with a data label
case class Store(dataLabel: DataLabel, value: CollectedValue[_]) extends KeywordsDatabaseAction

//store a List of CollectedValue associated with a data label
case class StoreList(dataLabel: DataLabel, value: List[CollectedValue[_]]) extends KeywordsDatabaseAction

//retrieve all CollectedValues associated with a data label
case class Retrieve(dataLabel: DataLabel) extends KeywordsDatabaseAction

//remove all the CollectedValues associated with a data label
case class Clean(dataLabel: DataLabel) extends KeywordsDatabaseAction