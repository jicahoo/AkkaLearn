package zzz.akka.avionics

import akka.actor.Props

/**
 * Created by jichao on 2015/11/3.
 */
object FlightAttendantPathChecker {
  def main(args: Array[String]) {
    val system = akka.actor.ActorSystem("PlaneSimulation")
    val lead = system.actorOf(Props(
      new LeadFlightAttendant with AttendantCreationPolicy),
      "LeadFlightAttendant")
    Thread.sleep(2000)
    println("Before Terminate!")
    system.terminate()
  }
}