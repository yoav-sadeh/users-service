package com.hamlazot.users.dal

import java.util.UUID

import com.datastax.driver.core.{SimpleStatement, Row}
import com.hamlazot.users.UsersRepositoryF.{Add, AddOrRemove, Remove}
import com.hamlazot.users.interpreters.ConcreteUser
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.builder.query.{QueryOptions, CQLQuery}
import com.websudos.phantom.builder.query.prepared.ExecutablePreparedQuery
import com.websudos.phantom.connectors.RootConnector
import com.websudos.phantom.dsl._

import scala.concurrent.Future

/**
 * @author yoav @since 11/11/16.
 */
class UsersTable extends CassandraTable[ConcreteUsersTable, ConcreteUser] {
  override def tableName: String = "users"

  object id extends UUIDColumn(this) with PartitionKey[UUID] {
    override lazy val name = "user_id"
  }

  object name extends StringColumn(this) //with ClusteringOrder[String]

  object trustees extends MapColumn[UUID, Int](this)

  object trusters extends MapColumn[UUID, Int](this)

  override def fromRow(r: Row): ConcreteUser = {
    val user = ConcreteUser(id(r), name(r), trustees(r), trusters(r))
    user
  }
}

abstract class ConcreteUsersTable extends UsersTable with RootConnector {

  def getByUserId(id: UUID): Future[Option[ConcreteUser]] = {
    val stmt = select
      .where(_.id eqs id)
    //.consistencyLevel_=(ConsistencyLevel.ONE)


    stmt.one()
  }

  def store(user: ConcreteUser): Future[ResultSet] = {
    insert
      .value(_.id, user.id)
      .value(_.name, user.name)
      .value(_.trustees, user.trustees)
      .value(_.trusters, user.trusters)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }

  def deleteById(id: UUID): Future[ResultSet] = {
    delete
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }

  def updateUserName(userId: UUID, name: String): Future[ResultSet] = {
    update
      .where(_.id eqs userId)
      .modify(_.name.setTo(name))
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }

  def updateUserTrustees(userId: UUID, tees: Map[UUID, Int], addOrRemove: AddOrRemove): Future[ResultSet] = {

      addOrRemove match {
        case Add =>
          val query = update
            .where(_.id eqs userId)
            .modify(col => col.trustees putAll tees)
            .consistencyLevel_=(ConsistencyLevel.ONE)

            query.future()

        case Remove =>
          val stmt = new SimpleStatement(s"UPDATE users SET trustees=trustees - {${tees.keys.mkString(",")}} WHERE user_id=$userId;")

          new ExecutablePreparedQuery(stmt, QueryOptions.empty.consistencyLevel_=(ConsistencyLevel.ONE)).future()

      }


  }

  def updateUserTrusters(userId: UUID, ters: Map[UUID, Int], addOrRemove: AddOrRemove): Future[ResultSet] = {
    addOrRemove match {
      case Add =>
        val query = update
          .where(_.id eqs userId)
          .modify(col => col.trustees putAll ters)
          .consistencyLevel_=(ConsistencyLevel.ONE)

        query.future()

      case Remove =>
        val stmt = new SimpleStatement(s"UPDATE users SET trusters=trusters - {${ters.keys.mkString(",")}} WHERE user_id=$userId;")

        new ExecutablePreparedQuery(stmt, QueryOptions.empty.consistencyLevel_=(ConsistencyLevel.ONE)).future()

    }
  }
}