import java.util;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

/*
	1. central lock Server node
	
	2. lock Agent runs on each client node
		--> each client node accesses the API through the Agent
	
	3. each lock Agent communicats over the network w/ the
		central lock Server

	4. Agent requests & releases the lock with
		--> LockMsg
		--> UnlockMsg

	5. Server sends a msg to notify Agent when it has received the lock
		--> GrantMsg
	
	** Mutual Exclusion **

		= only 1 node can hold lock at a time
*/

public class ActorNode extends AbstractActor{
	private final LoggingAdapter log = Logging.getLogger(context().system(), this);
	// "name" of each node
	public boolean isAgent;
	// each Agent indicates when holding lock
	public boolean holdsLock;
	// Server maintains a queue of Agent nodes
	List<ActorNode> queue;

	public ActorNode(String args){
		// matches types of messages actor can accept
		receive(ReceiveBuilder.
      		match(String.class, s -> {
        		log.info("Received String message: {}", s);
      		}).
      		matchAny(o -> log.info("received unknown message")).build()
    	);
		/* Don't think I need InitState since I initialize here */
    	holdsLock = false;
		queue = new ArrayList<ActorNode>();
		if(isAgent(args))
			isAgent = 1;
		else
			isAgent = 0;
	}

	void public State(Node args){
		// i. currently holding lock
		if(queue.indexOf(args) == 0)
			args.holdsLock = true;
		// ii. add to queue if not empty
		else{
			if(!queue.contains(args))
				queue.add(args);
		}
	}

	void HandleInp(Node args, String input){
		if(isAgent(args)){
			if(input.equals("LockMsg"))
				// TO-DO: tell Server to Lock
			else{
				if(args.holdsLock)
					args.holdsLock = false;
				// TO-DO: tell Server to Unlock
			}
		}
	}

	void HandleMsg(Node args, Node src, String input){
		// i. Agent
		if(isAgent(args)){
			// TO-DO: tell Server to Grant Msg
			args.holdsLock = true;
		}
		// ii. Server
		else{
			// LOCK
			if(input.equals("LockMsg")){
				// If lock not held, immediately grant lock
				if(s.queue.size() == 0)
					// TO-DO: tell Server to Grant Msg immediately
				s.queue.add(src);
			}
			// UNLOCK
			else{
				// Send head of queue to back -- no longer holds lock
				Node temp = s.queue.get(0);
				s.queue.remove(0);
				s.queue.add(temp);
				// Grant Lock to next waiting agent -- if any!
				if(s.queue.size() > 0)
					// TO-DO: GrantMsg to next waiting agent
			}
		}
	}
	
	boolean isAgent(String args){
		if(args.equals("Agent") || args.equals("agent"))
			return true;
		return false;
	}
}

