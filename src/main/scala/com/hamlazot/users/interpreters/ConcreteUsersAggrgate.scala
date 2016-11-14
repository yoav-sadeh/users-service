package com.hamlazot.users.interpreters

import java.util.UUID

import com.hamlazot.domain.common.users.UsersAggregate

/**
 * @author yoav @since 11/8/16.
 */
trait ConcreteUsersAggrgate extends UsersAggregate {
  override type User = ConcreteUser
  override type Trustees = Map[UserId, Int]
  override type Trusters = Map[UserId, Int]
  override type UserId = UUID
}

