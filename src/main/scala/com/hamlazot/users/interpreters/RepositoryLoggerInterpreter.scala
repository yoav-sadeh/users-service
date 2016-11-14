package com.hamlazot.users.interpreters

import com.hamlazot.users.DataDSL.{DataCall, DataOpteration, DataStoreRequest}
import com.hamlazot.users.FutureStringOr
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import scalaz.{Id, ~>}

/**
 * @author yoav @since 11/9/16.
 */
object RepositoryLoggerInterpreter extends (DataStoreRequest ~> Id.Id) with LazyLogging {
  override def apply[A](fa: DataStoreRequest[A]): Id.Id[A] = {

    fa match {
      case DataOpteration(operation) =>

        operation match {
          case a: DataCall[FutureStringOr[_]] =>
            logger.info(a.toString)
            Left(Future.successful(s"$a"))
        }
    }
  }
}
