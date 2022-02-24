/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.common

import akka.actor.typed.ActorRef

sealed trait Message

case class Bet(player: String, game: Int, amount: Int) extends Message

case class PlayerBet(id: Int, bet: Bet, sender: ActorRef[Message]) extends Message

case class ConfirmationMessage(id: Int) extends Message

case class RetrieveBets(sender: ActorRef[Message]) extends Message

case class RegisterProcessor(processor: ActorRef[Message]) extends Message

case class BetList(bets: List[Bet]) extends Message

case class Retry(id: Int) extends Message

object Bet {
  implicit object BetOrdering extends Ordering[Bet] {
    def compare(a: Bet, b: Bet): Int = {
      (a.player compare b.player) match {
        case 0 ⇒
          (a.game compare b.game) match {
            case 0 ⇒ a.amount compare b.amount
            case g ⇒ g
          }
        case p ⇒ p
      }
    }
  }
}

