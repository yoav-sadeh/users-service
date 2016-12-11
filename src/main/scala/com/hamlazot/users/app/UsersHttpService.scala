package com.hamlazot.users.app

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives.{as, decodeRequest, entity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.{MethodDirectives, MiscDirectives, PathDirectives, RouteDirectives}
import akka.stream.ActorMaterializer
import com.hamlazot.common.http.JsonMarshalling
import com.hamlazot.common.macros.Macros.Mapper

import com.hamlazot.users.interpreters.ConcreteUsersService
import com.typesafe.scalalogging.LazyLogging
import org.json4s.MappingException
import org.json4s.JsonAST.JObject

import scala.util.{Failure, Success, Try}
import scalaz.{Id, ~>}

/**
 * @author yoav @since 11/20/16.
 */


trait UsersHttpService extends PathDirectives
with MiscDirectives
with MethodDirectives
with RouteDirectives
with JsonMarshalling
with LazyLogging {

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val ec = system.dispatcher

  import com.hamlazot.common.macros.Macros.Serializer
  val service: ConcreteUsersService

  registerCustomKeySerializer[UUID](uuid => uuid.toString, str => UUID.fromString(str))

  val usersPrefix = pathPrefix("users")

  val route: Route = usersPrefix {

    post {
      decodeRequest {


       implicit val deserializer = implicitly[Serializer[service.CreateUserRequest]]
        entity(asTry[service.CreateUserRequest]) { tryRequest =>
          val tryResponse = tryRequest map (req => service.createUser(service.CreateUserRequest(req.name, req.trustees)))
          complete {
            tryResponse match {
              case Success(future) =>
                future.map { response =>
                  HttpResponse(Created, entity = HttpEntity(ContentTypes.`application/json`, seriamap(response)))
                }

              case Failure(e) =>
                e match {
                  case ex: MappingException =>
                    logger.warn(s"unprocessible request: $ex")
                    HttpResponse(UnprocessableEntity)
                  case _ =>
                    logger.error("Exception in createUser: ", e)
                    HttpResponse(InternalServerError)
                }
            }
          }
        }
      }
    }
  }

}

case class UserRequest[Trutees](name: String, trustees: Trutees)

case class UserResponse[UserId](userId: UserId)

