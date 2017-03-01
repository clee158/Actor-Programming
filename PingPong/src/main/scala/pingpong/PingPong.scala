package pingpong
import akka.actor._

case object PongReceive
case object PingStart
case object PongStop

/*
	ActorRef:
		immutable and serializable hand to an actor

	sender:
		gives you the ActorRef which was implicitly/explicitly picked
		up at the sending site
*/
class Ping(pong: ActorRef) extends Actor {
	var count = 0
	def receive = {
		case PingStart => {
			println("Ping")
			count += 1
			if (count > 3) {
				pong ! PongStop
				context.stop(self)
			}
			else
				pong ! PongReceive
		}
	}
}


class Pong extends Actor {
	def receive = {
		case PongReceive => {
			println("Pong")
			sender ! PingStart
		}
		case PongStop => {
			context.stop(self)
		}
	}
}

object PingPongActors extends App{
		
		/* need an ActorSystem to get started 
		
			actor system =	hierarchical group of actors which share 
							common configuration
		*/
		val system = akka.actor.ActorSystem("system")

		/* From the manuals...
			
			val props = Props[MyActor]
			system.actorOf(Props[MyActor], "name")
			system.actorOf(Props(classOf[MyActor], arg1, arg2), "name")

			Props:
				a configuration object using in creating an Actor
				immutable -- thread-safe & shareable
		*/
		val pong = system.actorOf(Props[Pong], name = "pong")
		val ping = system.actorOf(Props(new Ping(pong)), name = "ping")
		ping ! PingStart
}
