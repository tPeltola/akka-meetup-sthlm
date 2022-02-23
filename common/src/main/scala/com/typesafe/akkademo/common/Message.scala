/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.common

sealed trait Message

case class Bet(player: String, game: Int, amount: Int) extends Message

case class PlayerBet(id: Int, bet: Bet) extends Message

case class ConfirmationMessage(id: Int) extends Message

case object RetrieveBets extends Message

case object RegisterProcessor extends Message

case class BetList(bets: List[Bet]) extends Message

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

