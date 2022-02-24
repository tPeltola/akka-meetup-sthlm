/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.service

import akka.pattern.{ask, pipe}
import akka.actor.{Actor, ActorLogging}
import com.typesafe.akkademo.common.{Bet, Command, Request, ConfirmationMessage, PlayerBet, RegisterProcessor, RetrieveBets, Retry}
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import akka.actor.typed.scaladsl.adapter._

import scala.concurrent.duration._
import scala.util.Success


object BettingService {
  def apply(): Behavior[Request] = noProcessor(Map.empty, 1)

  def noProcessor(bets: Map[Int, Bet], sequence: Int): Behavior[Request] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case bet: Bet =>
        noProcessor(bets + (sequence -> bet), sequence + 1)
      case RegisterProcessor(processor) =>
        bets.foreach { case (id, bet) => processBet(context, processor, id, bet) }
        usingProcessor(bets, sequence, processor)
    }
  }


  def usingProcessor(bets: Map[Int, Bet], sequence: Int, processor: ActorRef[Command]): Behavior[Request] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case RegisterProcessor(registered) =>
        usingProcessor(bets, sequence, registered)
      case bet: Bet =>
        processBet(context, processor, sequence, bet)
        usingProcessor(bets + (sequence -> bet), sequence + 1, processor)
      case retrieve: RetrieveBets =>
        processor ! retrieve
        Behaviors.same
      case ConfirmationMessage(id) =>
        usingProcessor(bets - id, sequence, processor)
      case Retry(id) if bets.contains(id) =>
        processBet(context, processor, id, bets(id))
        Behaviors.same
      case Retry(b) =>
        Behaviors.same
    }
  }

  def processBet(context: ActorContext[Request], processor: ActorRef[Command], id: Int, bet: Bet): Unit = {
    implicit val timeout: Timeout = Timeout(5.seconds)
    context.ask(processor, replyTo => PlayerBet(id, bet, replyTo)) {
      case Success(ConfirmationMessage(id)) => ConfirmationMessage(id)
      case _ => Retry(id)
    }
  }

}
