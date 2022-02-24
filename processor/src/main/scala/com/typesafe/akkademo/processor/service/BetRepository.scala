package com.typesafe.akkademo.processor.service

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.akkademo.common.{Bet, BetList, ConfirmationMessage, Message, PlayerBet, RetrieveBets}
import com.typesafe.akkademo.processor.repository.ReallyUnstableResource


object BetRepository {
  def apply(resource: ReallyUnstableResource = new ReallyUnstableResource): Behavior[Message] = Behaviors.receiveMessage {
    case PlayerBet(id, Bet(player, game, amount), sender) =>
      resource.save(id, player, game, amount)
      sender ! ConfirmationMessage(id)
      Behaviors.same
    case RetrieveBets(sender) =>
      sender ! BetList(resource.findAll.toList)
      Behaviors.same
  }
}