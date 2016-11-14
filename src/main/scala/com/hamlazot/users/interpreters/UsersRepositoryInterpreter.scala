package com.hamlazot
package users
package interpreters

import com.datastax.driver.core.utils.UUIDs
import com.hamlazot.users.DataDSL.{DataOpteration, DataStoreRequest}

import com.hamlazot.users.UsersRepositoryF.DSL.{InsertUser, UserQuery, DeleteUser, UpdateUserName, UpdateUserTrustees, UpdateUserTrusters}
import com.hamlazot.users.dal.{Connector, ProductionDatabase}
import com.typesafe.scalalogging.LazyLogging
import com.websudos.phantom.connectors.KeySpace

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{Id, ~>}

/**
 * @author yoav @since 11/9/16.
 */
class UsersRepositoryInterpreter(implicit val ec: ExecutionContext) extends (DataStoreRequest ~> Id.Id) with LazyLogging with ProductionDatabase {

  override def apply[A](fa: DataStoreRequest[A]): Id.Id[A] = {
    fa match {
      case DataOpteration(operation) =>
        operation match {
          case InsertUser(user) =>
            val concreteUser = ConcreteUser(user.id, user.name, user.trustees, user.trusters)
            val result = database.usersModel.store(concreteUser) map {
              case rs =>
                if(rs.wasApplied())
                  ()
                else{
                  throw InsertFailedException(concreteUser)
                }

              case _ => throw InsertFailedException(concreteUser)
            }

            Right(result)
          case UserQuery(id) =>
            Right(database.usersModel.getByUserId(id).map(_.get))

          case DeleteUser(id) =>
            Right(database.usersModel.deleteById(id).map(f => ()))

          case UpdateUserName(id, name) =>
            Right(database.usersModel.updateUserName(id, name).map(f => ()))

          case UpdateUserTrustees(id, tees, addOrRemove) =>
            Right(database.usersModel.updateUserTrustees(id, tees, addOrRemove).map(f => ()))

          case UpdateUserTrusters(id, tees, addOrRemove) =>
            Right(database.usersModel.updateUserTrusters(id, tees, addOrRemove).map(f => ()))
        }
    }
  }
}

class UsersRepositoryInterpreterExcption(msg: String) extends Exception(msg)
case class InsertFailedException(user: ConcreteUser) extends UsersRepositoryInterpreterExcption(s"Failed to insert user: $user")


object Appapp extends App {
  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global
  val repo = new UsersRepositoryInterpreter
  val createQuery = repo.database.usersModel.autocreate(KeySpace("hamlazot")).queryString
  //repo.database.usersModel.autocreate(Connector.connector.provider.space)
  import com.hamlazot.users.UsersRepositoryF.AccountDataOperations.{insert,query, updateName, updateTrustees, updateTrusters}
  val uu = java.util.UUID.randomUUID()
  //runFC(insert(ConcreteUser(uu, "yoav", Map(uu -> 3), Map(uu -> 5))))(repo)
  val id = java.util.UUID.fromString("6db197f8-7679-474e-86ec-7b5c9899bee9")
  runFC(updateName(id, "bijo"))(repo)
  Thread.sleep(3000)
  val user = runFC(query(id))(repo)
  Thread.sleep(3000)
  println(user)
}