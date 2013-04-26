package com.typesafe.akkademo.processor.service

import akka.actor.Actor
import akka.actor.ActorLogging
import com.typesafe.akkademo.common.PlayerBet
import com.typesafe.akkademo.processor.repository.ReallyUnstableResource
import com.typesafe.akkademo.common.Bet
import com.typesafe.akkademo.common.RetrieveBets
import com.typesafe.akkademo.common.ConfirmationMessage

class BetRepository extends Actor with ActorLogging {
  val resource = new ReallyUnstableResource

  def receive = {
    case PlayerBet(id, Bet(player, game, amount)) ⇒ {
      resource.save(id, player, game, amount)
      sender ! ConfirmationMessage(id)
    }
    case RetrieveBets ⇒ sender ! resource.findAll.toList
  }
}