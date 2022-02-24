/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{Behavior, SupervisorStrategy}
import com.typesafe.akkademo.common.{Message, PlayerBet, RegisterProcessor, RetrieveBets}


object BettingProcessor {
  def apply(): Behavior[Message] = Behaviors.setup { context =>
    val service = context.toClassic.actorSelection(context.system.settings.config.getString("betting-service-actor"))
    val repo = context.spawn(Behaviors.supervise[Message](BetRepository()).onFailure(SupervisorStrategy.restart), "repo")

    service ! RegisterProcessor(context.self)

    Behaviors.receiveMessage[Message] {
      case bet: PlayerBet =>
        repo ! bet
        Behaviors.same
      case retrieve: RetrieveBets =>
        repo ! retrieve
        Behaviors.same
    }
  }
}
