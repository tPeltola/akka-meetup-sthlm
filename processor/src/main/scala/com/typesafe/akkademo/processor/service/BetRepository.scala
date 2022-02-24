package com.typesafe.akkademo.processor.service

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.akkademo.common._
import com.typesafe.akkademo.processor.repository.ReallyUnstableResource


object BetRepository {
  def apply(resource: ReallyUnstableResource = new ReallyUnstableResource): Behavior[Command] = Behaviors.receiveMessage {
    case PlayerBet(id, Bet(player, game, amount), sender) =>
      resource.save(id, player, game, amount)
      sender ! ConfirmationMessage(id)
      Behaviors.same
    case RetrieveBets(sender) =>
      sender ! BetList(resource.findAll.toList)
      Behaviors.same
  }
}