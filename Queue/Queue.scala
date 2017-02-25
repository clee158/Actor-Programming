import scala.collection.immutable.List

/*
Queue objects implement data structures that allow to insert and retrieve
elements in a first-in-first-out (FIFO) manner.

Queue is implemented as a pair of Lists, one containing the in elements and
the other the out elements.

Elements are added to the in list and removed from the out list.

When the out list runs dry, the queue is pivoted by replacing the out list by
in.reverse, and in by Nil.

Adding items to the queue always has cost O(1).

Removing items has cost O(1), except in the case where a pivot is required, in
which case, a cost of O(n) is incurred, where n is the number of elements in the queue.

When this happens, n remove operations with O(1) cost are guaranteed.
Removing an item is on average O(1).
 */

/*
  Syntax Juice

  Q[A <: B]   class Q can take any class A that is a subclass of B

  Q[+B]       Q can take any class, but if A is a subclass of B, then Q[A] is
              considered to be a subclass of Q[B]

  Q[+A <: B]  Q can only take subclasses of B as well as propagating the
              subclass relationship

 */
class Queue[+A](elem: A*) extends Seq[A]{

  protected val in : List[A]
  protected val out : List[A]

  /*
    Returns the first element in the queue, or throws an error if there is no
    element contained in the queue.
   */
  def front(): A = {
    return dequeue()
  }
  /*
    Returns a new queue with element added at the end of the old queue.
   */
  def enqueue[B >: A](elem: B): Queue[B]{

  }
  /*
    Returns a tuple with the first element in the queue, and a
    new queue with this element removed.
   */
  def dequeue: (A, Queue[A]) {

  }
  /*
    Checks if the queue is empty.
   */
  override def isEmpty: Boolean {
    return in.isEmpty && out.isEmpty
  }
}