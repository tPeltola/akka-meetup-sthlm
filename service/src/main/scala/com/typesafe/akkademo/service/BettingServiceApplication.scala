/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.service

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config._

object BettingServiceApplication extends App {
  val start = Behaviors.setup[NotUsed] { context =>
    context.spawn(BettingService(), "bettingService")
    Behaviors.empty
  }

  val system = ActorSystem(start, "BettingServiceActorSystem", ConfigFactory.load())
}
