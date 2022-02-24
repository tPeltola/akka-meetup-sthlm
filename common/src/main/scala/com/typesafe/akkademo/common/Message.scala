/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.common

import akka.actor.typed.ActorRef

// For configuring serialization
trait Message

sealed trait Command extends Message
sealed trait Request extends Message

case class PlayerBet(id: Int, bet: Bet, sender: ActorRef[ConfirmationMessage]) extends Command
case class RetrieveBets(sender: ActorRef[BetList]) extends Command with Request


case class RegisterProcessor(processor: ActorRef[Command]) extends Request
case class Bet(player: String, game: Int, amount: Int) extends Request
case class ConfirmationMessage(id: Int) extends Request

case class BetList(bets: List[Bet]) extends Message
case class Retry(id: Int) extends Request

object Bet {
  implicit object BetOrdering extends Ordering[Bet] {
    def compare(a: Bet, b: Bet): Int = {
      (a.player compare b.player) match {
        case 0 =>
          (a.game compare b.game) match {
            case 0 => a.amount compare b.amount
            case g => g
          }
        case p => p
      }
    }
  }
}

