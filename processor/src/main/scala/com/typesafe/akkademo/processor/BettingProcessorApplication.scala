/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import com.typesafe.config._
import com.typesafe.akkademo.processor.service.BettingProcessor

object BettingProcessorApplication extends App {
  val system = ActorSystem(setup, "BettingProcessorActorSystem", ConfigFactory.load())

  def setup: Behavior[NotUsed] = Behaviors.setup { context =>
    context.spawn(BettingProcessor(), "bettingProcessor")
    Behaviors.empty
  }
}
