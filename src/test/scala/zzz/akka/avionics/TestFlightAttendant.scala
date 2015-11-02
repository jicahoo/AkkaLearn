package zzz.akka.avionics

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestKit, TestActorRef, ImplicitSender}
import org.scalatest.WordSpecLike
import org.scalatest.matchers.MustMatchers

import com.typesafe.config.ConfigFactory

object TestFlightAttendant {
  def apply() = new FlightAttendant with AttendantResponsiveness {
    val maxResponseTimeMS = 1
  }
}

class FlightAttendantSpec
  extends TestKit(ActorSystem("FlightAttendantSpec", ConfigFactory.parseString("akka.scheduler.tick-duration = 10ms")))
                          with ImplicitSender with WordSpecLike with MustMatchers {
  //On windows, the min value of akka.scheduler.tick-duration is 10ms
  import FlightAttendant._
  "FlightAttendant" should {
    "get a drink when asked" in {
      val a = TestActorRef(Props(TestFlightAttendant()))
      a ! GetDrink("Soda")
      expectMsg(Drink("Soda"))
    }
  }
}