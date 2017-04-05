import akka.actor._
import scala.collection.mutable.Queue

/* - let Agent class take an output ActorRef to send outputs to */

/*
 *  [ Name :=   Server | Agent(n) ]
 *
 *  Name lists the names of nodes in the system.
 *
 *  There is 
 *
 *    1) a single Server node 
 *    2) an arbitrary # of Agents
 *
 *
 *  [ State ]
 *
 *  Defines the state maintained at each node.
 *  Node's Name determines the data maintained locally at that node.
 *
 *  Server := maintains a [queue] of Agent nodes
 *    - initially empty
 *    - head: Agent holding the lock
 *    - rest: Agents waiting to acquire the lock
 *
 *  Agent := maintains a [boolean]
 *    - initially false
 *    - true when Agent holds the lock
 */

abstract class Name extends Actor{}

class Server extends Name{
  val state :Queue[Agent] = new Queue()
  def receive = {
    case LockMsg => {
      /* Does locking just consist of updating the state? */
      if (this.head().getState() == false)
        this.head().setState(true)
    }
    case UnlockMsg => {
      /* Since I already set the state to false,
       Do I need to shiftHead here? */
    }
    case GrantedMsg(a) =>{
      /* Same concerns as above! */
      if(a.getState() == 0)
        a.setState(true)
    }
  } 
  def queueEmpty() : Boolean = {
    return state.isEmpty
  }
  def addAgent(a: Agent) : Unit = {
    state += (a)
  }
  def shiftHead = {
    val oldhead = state.dequeue
    state += (oldhead)
  }
  def head() : Agent = {
    return state.front
  }
}

class Agent(server: ActorRef) extends Name{
  var state : Boolean = false
  var server : Server = server

  def receieve = {}
  def getState() : Boolean = {
    return state
  }
  def setState(b: Boolean) : Unit = {
    state = b
  }
  def getServer() : Server = {
    return server
  }
}


/*  
 *  [ Input := Lock | Unlock ]
 *
 *  External I/O messgaes exchanged btwn Agent & other local processes on its node
 */
trait Input
case object Lock extends Input
case object Unlock extends Input

/*  
 *  [ Msg := LockMsg | UnlockMsg | GrantedMsg ]
 *
 *  Network messages exchanged between Agents and the central Server
 */
abstract class Msg
case object LockMsg extends Msg
case object UnlockMsg extends Msg
case class GrantedMsg(src: Agent) extends Msg

object Handler{
  
  def HandleInput(n: Name, inp: Input) : Unit = {
    n match {
      case s: Server => {}
      case a: Agent => {
        inp match {
          case Lock =>{
            n.getServer() ! LockMsg
          }
          case Unlock => {
            if(n.getState() == true)
              n.setState(false)
            n.getServer() ! UnlockMsg
          }
        }
      } 
    }
  }

  def HandleMsg(n: Name, src: Name, msg: Msg) : Unit = {
    n match {
      case s: Server => {
        msg match {
          case LockMsg => {
            if(n.queueEmpty())
              n ! GrantedMsg(src) // grant lock to src if lock not held
            n.addAgent(src)
          }
          case UnlockMsg => {
            n.shiftHead()
            if(!n.queueEmpty())
              n ! GrantedMsg(n.head) // grant lock to new head
          }
        } 
      }
      case a: Agent => {
        msg match {
          case GrantedMsg(_) => {
            n.setState(true)
            // output Granted
          }
        } 
      }
    }
  }
}
