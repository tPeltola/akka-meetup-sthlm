package com.typesafe.akkademo.processor.service

import akka.actor.Actor
import akka.actor.ActorLogging
import com.typesafe.akkademo.common.{Bet, BetList, ConfirmationMessage, PlayerBet, RetrieveBets}
import com.typesafe.akkademo.processor.repository.ReallyUnstableResource

class BetRepository extends Actor with ActorLogging {
  val resource = new ReallyUnstableResource

  def receive = {
    case PlayerBet(id, Bet(player, game, amount)) ⇒ {
      resource.save(id, player, game, amount)
      sender ! ConfirmationMessage(id)
    }
    case RetrieveBets ⇒ sender ! BetList(resource.findAll.toList)
  }
}