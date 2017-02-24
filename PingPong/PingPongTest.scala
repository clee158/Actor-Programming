import pingpong.Ping
import akka.actor._
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class PingPongTest extends TestKit(ActorSystem("MyPingPong")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll{

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A PingPong Actor" must {
    "send back messages" in {
      val actorRef = TestActorRef[pingpong.Ping]
      actorRef ! PingStart
      expectMsg("Ping" +
        "Pong" +
        "Ping" +
        "Pong" +
        "Ping" +
        "Pong" +
        "Ping")
    }
  }
}