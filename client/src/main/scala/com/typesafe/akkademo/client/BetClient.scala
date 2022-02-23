/**
 * Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.client

import akka.actor.ActorSystem

import scala.concurrent.duration._
import scala.concurrent.Await
import akka.util.Timeout
import akka.pattern.ask
import com.typesafe.config.ConfigFactory
import com.typesafe.akkademo.common.{Bet, BetList, RetrieveBets}

object BetClient extends App {

  println("*** STARTING TEST OF BETTING APPLICATION")

  val config = ConfigFactory.parseString("""
    akka {
      actor {
        provider = remote
        serializers {
          jackson-json = "akka.serialization.jackson.JacksonJsonSerializer"
        }
        serialization-bindings {
          "com.typesafe.akkademo.common.Message" = jackson-json
        }
      }
      remote {
        artery {
          transport = tcp
          canonical.hostname = "127.0.0.1"
          canonical.port = 2661
        }
      }
    }""")

  val system = ActorSystem("TestActorSystem", ConfigFactory.load(config))
  val service = system.actorSelection("akka://BettingServiceActorSystem@127.0.0.1:2552/user/bettingService")

  try {
    // create the list of bets
    val bets = (1 to 200).map(p ⇒ Bet("ready_player_one", p % 10 + 1, p % 100 + 1))

    args match {
      case Array("send") =>
        bets.foreach(bet ⇒ service ! bet)
        println("*** SENDING OK")
        Thread.sleep(10000)
      case _ =>
        implicit val timeout: Timeout = 2.seconds
        val fBets = ask(service, RetrieveBets).mapTo[BetList]
        val result = Await.result(fBets, 5.seconds).bets.sorted
        assert(result == bets.sorted, s"expected ${bets.sorted}, got $result")
        println("*** TESTING OK")
    }
  } finally {
    system.terminate()
  }
}
