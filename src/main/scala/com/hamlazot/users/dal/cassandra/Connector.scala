package com.hamlazot.users.dal.cassandra

import java.net.InetAddress

import com.typesafe.config.ConfigFactory
import com.websudos.phantom.connectors.ContactPoints

import scala.collection.JavaConversions._

object Connector {
  val config = ConfigFactory.load()

  val hosts = config.getStringList("cassandra.host")
  val inets = hosts.map(InetAddress.getByName)

  val keyspace: String = config.getString("cassandra.keyspace")

  /**
    * Create a connector with the ability to connects to
    * multiple hosts in a secured cluster
    */
  lazy val connector = ContactPoints(hosts).withClusterBuilder(
    _.withCredentials(
      config.getString("cassandra.username"),
      config.getString("cassandra.password")
    )
  ).keySpace(keyspace)

}