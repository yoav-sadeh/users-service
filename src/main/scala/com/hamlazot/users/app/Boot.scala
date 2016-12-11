package com.hamlazot.users.app

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.hamlazot.users.DataDSL.DataStoreRequest
import com.hamlazot.users.dal.cassandra.{ProductionDb, UsersDatabase}
import com.hamlazot.users.interpreters.{ConcreteUsersService, RepositoryLoggerInterpreter, UsersRepositoryInterpreter}

import scala.concurrent.ExecutionContext
import scala.io.StdIn
import scalaz.{Id, ~>}

/**
 * @author yoav @since 11/8/16.
 */
object Boot extends App with UsersHttpService {

  override implicit lazy val system = ActorSystem("users-service")
  override implicit lazy val materializer = ActorMaterializer()

  object ProductionUsersRepository extends UsersRepositoryInterpreter {
    override implicit val ec: ExecutionContext = system.dispatcher

    override def database: UsersDatabase = ProductionDb
  }

  override implicit val service: ConcreteUsersService = ConcreteUsersService
  object ConcreteUsersService extends ConcreteUsersService {
    override protected val dbLogger: ~>[DataStoreRequest, Id.Id] = RepositoryLoggerInterpreter
    override protected val dbDriver: ~>[DataStoreRequest, Id.Id] = ProductionUsersRepository
    override implicit val ec: ExecutionContext = system.dispatcher
  }

  val routi = Route.handlerFlow(route)
  val bindingFuture = Http().bindAndHandle(routi, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.terminate()) // and shutdown when done

}

