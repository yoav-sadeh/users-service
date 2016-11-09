package com.hamlazot


import scala.concurrent.Future
import scalaz.{Monad, ~>, Coyoneda, Free}

/**
 * @author yoav @since 11/9/16.
 */
package object users {

  object DataDSL {

    sealed trait DataStoreRequest[A]

    trait DataCall[+A]

    final case class DataOpteration[A, Call[+A] <: DataCall[A]](dataCall: Call[A]) extends DataStoreRequest[A]

    trait DataOperations {
      def dataOperation[A, Call[+A] <: DataCall[A]](service: Call[A]): Free[Fetchable, A] =
        liftFC(DataOpteration(service): DataStoreRequest[A])
    }

    type Fetchable[A] = Coyoneda[DataStoreRequest, A]
  }

  type FreeC[S[_], A] = Free[({type f[x] = Coyoneda[S, x]})#f, A]

  def liftFC[S[_], A](s: S[A]): FreeC[S, A] =
    Free.liftFU(Coyoneda lift s)

  def runFC[S[_], M[_], A](sa: FreeC[S, A])(interp: S ~> M)(implicit M: Monad[M]): M[A] =
    sa.foldMap[M](new (({type λ[α] = Coyoneda[S, α]})#λ ~> M) {
      def apply[A](cy: Coyoneda[S, A]): M[A] =
        M.map(interp(cy.fi))(cy.k)
    })

  type StringOr[A] = Either[String, A]
  type FutureStringOr[A] = Either[Future[String], Future[A]]

}
