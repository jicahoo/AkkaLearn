package zzz.akka.avionics

import akka.actor.{Props, Actor, ActorLogging}
import zzz.akka.avionics.EventSource.RegisterListener

object Plane {

  // Returns the control surface to the Actor that asks for them
  case object GiveMeControl

}

// We want the Plane to own the Altimeter and we're going to do that
// by passing in a specific factory we can use to build the Altimeter
class Plane extends Actor with ActorLogging {

  import Altimeter._
  import Plane._



  override def preStart() {
    // Register ourself with the Altimeter to receive updates on our altitude
    altimeter ! EventSource.RegisterListener(self)
    List(pilot, copilot) foreach {
      _ ! Pilots.ReadyToGo
    }
  }

  val altimeter = context.actorOf(Props(new Altimeter with ProductionEventSource))
  val controls = context.actorOf(Props(new ControlSurfaces(altimeter)))
  val config = context.system.settings.config
  val pilot = context.actorOf(Props[Pilot],
    config.getString("zzz.akka.avionics.flightcrew.pilotName"))
  val copilot = context.actorOf(Props[CoPilot],
    config.getString("zzz.akka.avionics.flightcrew.copilotName"))
  val autopilot = context.actorOf(Props[AutoPilot], "AutoPilot")
  val flightAttendant = context.actorOf(Props(LeadFlightAttendant()),
    config.getString("zzz.akka.avionics.flightcrew.leadAttendantName"))

  def receive = {
    case GiveMeControl =>
      log.info("Plane giving control.")
      sender ! controls
    case AltitudeUpdate(altitude) =>
      log.info(s"Altitude is now: $altitude")
  }

}