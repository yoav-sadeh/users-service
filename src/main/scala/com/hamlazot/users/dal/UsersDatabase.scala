package com.hamlazot.users.dal

import Connector._
import com.websudos.phantom.database.Database
import com.websudos.phantom.dsl.KeySpaceDef

/**
 * @author yoav @since 11/11/16.
 */
class UsersDatabase (override val connector: KeySpaceDef) extends Database[UsersDatabase](connector) {
  object usersModel extends ConcreteUsersTable with connector.Connector
}

/**
 * This is the production database, it connects to a secured cluster with multiple contact points
 */
object ProductionDb extends UsersDatabase(connector)

trait ProductionDatabaseProvider {
  def database: UsersDatabase
}

trait ProductionDatabase extends ProductionDatabaseProvider {
  override val database = ProductionDb
}
