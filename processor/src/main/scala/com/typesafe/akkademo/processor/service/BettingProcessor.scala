/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{Behavior, SupervisorStrategy}
import com.typesafe.akkademo.common.{Command, PlayerBet, RegisterProcessor, RetrieveBets}


object BettingProcessor {
  def apply(): Behavior[Command] = Behaviors.setup { context =>
    val service = context.toClassic.actorSelection(context.system.settings.config.getString("betting-service-actor"))
    val repo = context.spawn(Behaviors.supervise(BetRepository()).onFailure(SupervisorStrategy.restart), "repo")

    service ! RegisterProcessor(context.self)

    Behaviors.receiveMessage {
      case bet: PlayerBet =>
        repo ! bet
        Behaviors.same
      case retrieve: RetrieveBets =>
        repo ! retrieve
        Behaviors.same
    }
  }
}
