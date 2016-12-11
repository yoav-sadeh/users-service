package com.hamlazot.users.tests.specs

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.hamlazot.users.DataDSL.DataStoreRequest
import com.hamlazot.users.interpreters.{ConcreteUsersService, RepositoryLoggerInterpreter}
import com.hamlazot.users.tests.ctxt
import com.hamlazot.users.tests.interpreters.InMemUsersRepoInterpreter
import org.specs2.mutable.Specification

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.Try
import scalaz.{Id, ~>}

/**
 * @author yoav @since 11/16/16.
 */
class UsersServiceSpec extends Specification {

  object ConcreteUsersService extends ConcreteUsersService {
    override protected val dbDriver: ~>[DataStoreRequest, Id.Id] = InMemUsersRepoInterpreter
    override protected val dbLogger: ~>[DataStoreRequest, Id.Id] = RepositoryLoggerInterpreter
    override implicit val ec: ExecutionContext = ctxt
  }

  val service = ConcreteUsersService

  "service" should {
    "create user with no trustees" in {
      val request = ConcreteUsersService.CreateUserRequest("yoav", Map.empty[UUID, Int])
      val tryGetResponse = Try(Await.result(ConcreteUsersService.createUser(request), Duration(5, TimeUnit.SECONDS)))

      tryGetResponse.isSuccess shouldEqual true
    }
  }
}
