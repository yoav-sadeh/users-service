package com.hamlazot.users

import com.hamlazot.users.dal.{UsersDatabase, DatabaseProvider}

/**
 * @author yoav @since 11/14/16.
 */
package object tests {
  import java.net.InetAddress

  import com.typesafe.config.ConfigFactory
  import com.websudos.phantom.connectors.{ContactPoint, ContactPoints}

  import scala.collection.JavaConversions._

  val ctxt = scala.concurrent.ExecutionContext.Implicits.global

  object Connector {
    val config = ConfigFactory.load()

    //val hosts = config.getStringList("cassandra.host")
    //val inets = hosts.map(InetAddress.getByName)

    val keyspace: String = config.getString("cassandra.keyspace")

    /**
     * Create an embedded connector, used for testing purposes
     */
    lazy val testConnector = ContactPoint.local.noHeartbeat().keySpace(s"${keyspace}_test")
  }

  object TestDb extends UsersDatabase(Connector.testConnector)
  trait TestDatabase extends DatabaseProvider {
    override val database = TestDb
  }
}
