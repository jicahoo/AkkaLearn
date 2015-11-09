package zzz.akka.avionics

/**
 * Created by jichao on 2015/11/3.
 */

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef}

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

object Pilots {

  case object ReadyToGo

  case object RelinquishControl

  val selectionTimeOut = FiniteDuration(10,TimeUnit.SECONDS) //So long time, if set as 5 seconds. Always timeout why?

}

class Pilot extends Actor {

  import Pilots._
  import Plane._

  var controls: ActorRef = context.system.deadLetters
  var copilot: ActorRef = context.system.deadLetters
  var autopilot: ActorRef = context.system.deadLetters
  val copilotName = context.system.settings.config.getString("zzz.akka.avionics.flightcrew.copilotName")

  def receive = {
    case ReadyToGo =>
      context.parent ! Plane.GiveMeControl
      autopilot = Await.result(context.actorSelection("../AutoPilot").resolveOne(selectionTimeOut),selectionTimeOut)
      //copilot = context.actorSelection("../" + copilotName).resolveOne(selectionTimeOut).value.get.get  Why not work
      copilot = Await.result(context.actorSelection("../" + copilotName).resolveOne(selectionTimeOut),selectionTimeOut)
      println(copilotName)
    //case Controls(controlSurfaces) =>  Cannot work TODO
    //  controls = controlSurfaces
  }
}

class CoPilot extends Actor {

  import Pilots._

  var controls: ActorRef = context.system.deadLetters
  var pilot: ActorRef = context.system.deadLetters
  var autopilot: ActorRef = context.system.deadLetters
  val pilotName = context.system.settings.config.getString("zzz.akka.avionics.flightcrew.pilotName")

  def receive = {
    case ReadyToGo =>
      val pathPrefix = self.path.parent.toStringWithoutAddress+"/"
      pilot = Await.result(context.actorSelection( pathPrefix + pilotName).resolveOne(selectionTimeOut),selectionTimeOut)
      autopilot = Await.result(context.actorSelection(pathPrefix + "AutoPilot").resolveOne(selectionTimeOut), selectionTimeOut)
  }
}

class AutoPilot extends Actor {

  import Pilots._

  var controls: ActorRef = context.system.deadLetters
  var pilot: ActorRef = context.system.deadLetters
  var coPilot: ActorRef = context.system.deadLetters
  val pilotName = context.system.settings.config.getString("zzz.akka.avionics.flightcrew.pilotName")
  val coPilotName = context.system.settings.config.getString("zzz.akka.avionics.flightcrew.copilotName")


  def receive = {
    case ReadyToGo =>
      pilot = Await.result(context.actorSelection("../" + pilotName).resolveOne(selectionTimeOut),selectionTimeOut)
      coPilot = Await.result(context.actorSelection("../"+coPilotName).resolveOne(selectionTimeOut), selectionTimeOut)
  }
}

trait PilotProvider {
  def pilot: Actor = new Pilot

  def copilot: Actor = new CoPilot

  def autopilot: Actor = new AutoPilot
}