import pingpong._
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
      val pongRef = TestActorRef(new pingpong.Pong)
      val pingRef = TestActorRef(new pingpong.Ping(pongRef))
      pingRef ! PingStart
      expectMsg(PongStop)
    }
  }
}
