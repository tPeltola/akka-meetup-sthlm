/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.service

import akka.pattern.{ask, pipe}
import akka.actor.{Actor, ActorLogging}
import com.typesafe.akkademo.common.{Bet, RetrieveBets}
import akka.actor.ActorRef
import com.typesafe.akkademo.common.RegisterProcessor
import com.typesafe.akkademo.common.PlayerBet
import akka.util.Timeout

import scala.concurrent.duration._
import com.typesafe.akkademo.common.ConfirmationMessage

case class Retry(bet: PlayerBet)

class BettingService extends Actor with ActorLogging {
  import context.dispatcher

  var processor: Option[ActorRef] = None
  var bets: Map[Int, PlayerBet] = Map()
  var sequence = 1

  implicit val timeout = Timeout(5 seconds)

  def receive = {
    case RegisterProcessor ⇒ {
      processor = Some(sender)

      for (b ← bets.values) processBet(b)
    }
    case bet: Bet ⇒ {
      val player = PlayerBet(sequence, bet)
      sequence += 1
      processor match {
        case Some(p) ⇒ processBet(player)
        case None    ⇒ bets = bets + (player.id -> player)
      }
    }
    case RetrieveBets ⇒ for (p ← processor) p.forward(RetrieveBets)
    case ConfirmationMessage(id) ⇒ bets = bets - id
    case Retry(b) => context.system.scheduler.scheduleOnce(5 seconds) {
      processBet(b)
    }
  }

  def processBet(b: PlayerBet): Unit = {
    val future = processor.get ? b
    future.recover { case _ => Retry(b) }.pipeTo(self)
  }
}
