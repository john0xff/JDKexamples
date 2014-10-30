package contiune;

public class Snippet
{
	public static void main(String[] args)
	{
		test:
			for (int i = 0; i < 3; i++)
			{
				for (int j = 0; j < 10; ++j)
				{
					if(j == 5)
						continue test;
					else
						System.out.println(j);
				}
			}
			
	}

// part of ConcurrentLinkedDeque
//  private void linkFirst(E e) {
	
//	  restartFromHead:
//	        for (;;)
//	            for (Node<E> h = head, p = h, q;;) {
//	                if ((q = p.prev) != null &&
//	                    (q = (p = q).prev) != null)
//	                    // Check for head updates every other hop.
//	                    // If p == q, we are sure to follow head instead.
//	                    p = (h != (h = head)) ? h : q;
//	                else if (p.next == p) // PREV_TERMINATOR
//	                    continue restartFromHead;
//	                else {
//	                    // p is first node
//	                    newNode.lazySetNext(p); // CAS piggyback
//	                    if (p.casPrev(null, newNode)) {
//	                        // Successful CAS is the linearization point
//	                        // for e to become an element of this deque,
//	                        // and for newNode to become "live".
//	                        if (p != h) // hop two nodes at a time
//	                            casHead(h, newNode);  // Failure is OK.
//	                        return;
//	                    }
//	                    // Lost CAS race to another thread; re-read prev
//	                }
//	            }
}

