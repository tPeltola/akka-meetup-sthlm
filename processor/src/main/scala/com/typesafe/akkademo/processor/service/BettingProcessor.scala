/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor.service

import akka.actor.{ ActorLogging, Actor }
import akka.actor.SupervisorStrategy._
import akka.pattern.{ ask, pipe }
import akka.dispatch.MessageDispatcher
import com.typesafe.akkademo.common.{ PlayerBet, RetrieveBets }
import com.typesafe.akkademo.common.RegisterProcessor
import akka.actor.Props
import akka.util._
import scala.concurrent.duration._
import akka.dispatch.MessageDispatcher
import com.typesafe.akkademo.common.ConfirmationMessage
import akka.actor.OneForOneStrategy
import com.typesafe.akkademo.processor.repository.DatabaseFailureException

class BettingProcessor extends Actor with ActorLogging {
  import context.dispatcher

  val service = context.actorFor(context.system.settings.config.getString("betting-service-actor"))
  val repo = context.actorOf(Props[BetRepository], "repo")
  implicit val timeout = Timeout(5 seconds)

  override def preStart = {
    service ! RegisterProcessor
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _: RuntimeException         ⇒ Restart
      case _: DatabaseFailureException ⇒ Restart
    }

  def receive = {
    case bet: PlayerBet ⇒ repo.forward(bet)
    case RetrieveBets   ⇒ repo.forward(RetrieveBets)
  }
}
