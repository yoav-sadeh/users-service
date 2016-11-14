package com.hamlazot.users

/**
 * @author yoav @since 11/14/16.
 */
package object tests {
  import java.net.InetAddress

  import com.typesafe.config.ConfigFactory
  import com.websudos.phantom.connectors.{ContactPoint, ContactPoints}

  import scala.collection.JavaConversions._

  object Connector {
    val config = ConfigFactory.load()

    val hosts = config.getStringList("cassandra.host")
    val inets = hosts.map(InetAddress.getByName)

    val keyspace: String = config.getString("cassandra.keyspace")

    /**
     * Create an embedded connector, used for testing purposes
     */
    lazy val testConnector = ContactPoint.embedded.noHeartbeat().keySpace(s"${keyspace}_test")
  }
}
