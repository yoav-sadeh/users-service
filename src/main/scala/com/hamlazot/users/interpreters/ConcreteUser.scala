package com.hamlazot.users.interpreters

import java.util.UUID
/**
 * @author yoav @since 11/9/16.
 */
case class ConcreteUser(id:UUID, name: String, trustees: List[UUID], trusters: List[UUID])
