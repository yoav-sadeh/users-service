package com.hamlazot
package users

import java.util.UUID
import DataDSL._
import com.hamlazot.users.interpreters.ConcreteUsersAggrgate

/**
 * @author yoav @since 11/9/16.
 */
object UsersRepositoryF extends ConcreteUsersAggrgate{
  object DSL {

    sealed trait AccountDataCall[+A] extends DataCall[A]

    final case class UserQuery(id: UUID) extends AccountDataCall[FutureStringOr[User]]

    final case class InsertUser(user: User) extends AccountDataCall[FutureStringOr[Unit]]

    final case class UpdateUserName(id: UUID, name:String) extends AccountDataCall[FutureStringOr[User]]

    final case class UpdateUserTrustees(id: UUID, trustees: Trustees, addOrRemove: AddOrRemove) extends AccountDataCall[FutureStringOr[User]]

    final case class UpdateUserTrusters(id: UUID, trusters: Trusters, addOrRemove: AddOrRemove) extends AccountDataCall[FutureStringOr[User]]

    final case class DeleteUser(id: UUID) extends AccountDataCall[FutureStringOr[Unit]]

  }

   object AccountDataOperations extends DataOperations {

    import DSL._

    def query(id: UUID) = dataOperation(UserQuery(id))

    def insert[A](user: User) = dataOperation(InsertUser(user))

    def updateName[A](id: UUID, name:String) = dataOperation(UpdateUserName(id, name))

    def updateTrustees[A](id: UUID, trustees: Trustees, addOrRemove: AddOrRemove) = dataOperation(UpdateUserTrustees(id, trustees, addOrRemove))

    def updateTrusters[A](id: UUID, trusters: Trusters, addOrRemove: AddOrRemove) = dataOperation(UpdateUserTrusters(id, trusters, addOrRemove))

    def delete[A](id: UUID) = dataOperation(DeleteUser(id))

  }

  sealed trait AddOrRemove
  case object Add extends AddOrRemove
  case object Remove extends AddOrRemove
}
