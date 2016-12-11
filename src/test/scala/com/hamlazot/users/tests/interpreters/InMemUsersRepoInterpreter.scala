package com.hamlazot
package users.tests
package interpreters

import com.hamlazot.users.DataDSL.{DataOpteration, DataStoreRequest}
import com.hamlazot.users.dal.UsersRepositoryF.DSL._
import com.hamlazot.users.dal.UsersRepositoryF.{Add, Remove}
import com.hamlazot.users.interpreters.{InsertFailedException, ConcreteUser}
import com.typesafe.scalalogging.LazyLogging
import java.util.UUID
import scala.collection.mutable
import scala.concurrent.Future
import scalaz.{Id, ~>}

/**
 * @author yoav @since 11/17/16.
 */
object InMemUsersRepoInterpreter extends (DataStoreRequest ~> Id.Id) with LazyLogging{

  private val users = mutable.HashMap.empty[UUID, ConcreteUser]

  implicit val ec = ctxt
  override def apply[A](fa: DataStoreRequest[A]): Id.Id[A] = {
    fa match {
      case DataOpteration(operation) =>
        operation match {
          case InsertUser(user) =>
            val concreteUser = ConcreteUser(user.id, user.name, user.trustees, user.trusters)
            val result =  Future{
              try{
                users.put(user.id, user) match {
              case opt =>
                  ()

              case None => throw InsertFailedException(concreteUser)
            }

            }
              catch{
                case InsertFailedException(any) =>

                case e: Throwable =>
                    throw RepositoryException(operation, e)

              }
            }

            Right(result)

          case UserQuery(id) =>
            Right(Future{
              users(id)
            })

          case DeleteUser(id) =>
            Right(Future(users.remove(id)).map(r => ()))

          case UpdateUserName(id, name) =>
            Right(Future(users.update(id, users(id).copy(name = name))).map(f => ()))

          case UpdateUserTrustees(id, tees, addOrRemove) =>
            def update = addOrRemove match {
              case Add => users(id).copy(trustees = users(id).trustees ++ tees)
              case Remove => users(id).copy(trustees = users(id).trustees.filterNot(p => tees.contains(p._1)))
            }
            Right(Future(users.update(id, update)).map(f => ()))

          case UpdateUserTrusters(id, ters, addOrRemove) =>
            def update = addOrRemove match {
              case Add => users(id).copy(trusters = users(id).trusters ++ ters)
              case Remove => users(id).copy(trusters = users(id).trusters.filterNot(p => ters.contains(p._1)))
            }
            Right(Future(users.update(id, update)).map(f => ()))
        }
    }
  }
}
