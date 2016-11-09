package com.hamlazot.users
package interpreters

import com.hamlazot.domain.common.users.UsersService
import com.hamlazot.users.DataDSL.{Fetchable, DataStoreRequest}
import com.hamlazot.users.UsersRepositoryF.AccountDataOperations._
import com.hamlazot.users.UsersRepositoryF.{Add,Remove}
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.{ExecutionContext, Future}
import scalaz.{Free, Id, ~>}
import java.util.UUID
/**
 * @author yoav @since 11/8/16.
 */
trait ConcreteUsersService extends UsersService with ConcreteUsersAggrgate with LazyLogging{
  override type Operation[A, B] = A => Future[B]

  protected val dbDriver: (DataStoreRequest ~> Id.Id)
  protected val dbLogger: (DataStoreRequest ~> Id.Id)
  implicit val ec: ExecutionContext

  override def createUser: (CreateUserRequest) => Future[CreateUserResponse] = { request =>

      val user = ConcreteUser(UUID.randomUUID(), request.name, request.trustees, Nil)
      val inserted = insert(user)
      logDataOperation(inserted)

      val dbResult = runFC(inserted)(dbDriver).right.get
      dbResult.map(p => CreateUserResponse(user.id))
  }

  override def deleteUser: (DeleteUserRequest) => Future[DeleteUserResponse] = { request => 
    val deleted = delete(request.userId)
    logDataOperation(deleted)
    val dbResult = runFC(deleted)(dbDriver).right.get
    dbResult.map(p => DeleteUserResponse(true)) //TODO: return an eff monad to stack Try[Future]
  }

  override def removeTrustees: (RemoveTrusteesRequest) => Future[RemoveTrusteesResponse] = { request =>
    val updated = updateTrustees(request.userId, request.trustees, Remove)
    logDataOperation(updated)
    val dbResult = runFC(updated)(dbDriver).right.get
    dbResult.map(p => RemoveTrusteesResponse(true))
  }

  override def getUser: (GetUserRequest) => Future[GetUserResponse] = { request =>
    val fetched = query(request.userId)
    logDataOperation(fetched)
    val dbResult = runFC(fetched)(dbDriver).right.get
    dbResult.map(u => GetUserResponse(u))
  }

  override def removeTrusters: (RemoveTrustersRequest) => Future[RemoveTrustersResponse] = { request =>
    val updated = updateTrusters(request.userId, request.trusters, Remove)
    logDataOperation(updated)
    val dbResult = runFC(updated)(dbDriver).right.get
    dbResult.map(p => RemoveTrustersResponse(true))
  }

  override def addTrustees: (AddTrusteesRequest) => Future[AddTrusteesResponse] = { request =>
    val added = updateTrustees(request.userId, request.trustees, Add)
    logDataOperation(added)
    val dbResult = runFC(added)(dbDriver).right.get
    dbResult.map(p => AddTrusteesResponse(true))
  }

  override def addTrusters: (AddTrustersRequest) => Future[AddTrustersResponse] = { request =>
    val updated = updateTrusters(request.userId, request.trusters, Add)
    logDataOperation(updated)
    val dbResult = runFC(updated)(dbDriver).right.get
    dbResult.map(p => AddTrustersResponse(true))
  }


  def logDataOperation[A](opration: Free[Fetchable, FutureStringOr[A]]): Unit= {
    val dbLog = runFC(opration)(dbLogger).left.get
    dbLog.map(log => {

      logger.info(log)
    })

  }
}
