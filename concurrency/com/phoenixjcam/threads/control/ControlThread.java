package com.phoenixjcam.threads.control;

/**
 * <p>
 * Core Java provides a complete control over multithreaded program. You can develop a multithreaded program which can
 * be suspended, resumed or stopped completely based on your requirements. There are various static methods which you
 * can use on thread objects to control their behavior. Following table lists down those methods:
 * <p>
 * 1 public void suspend() This method puts a thread in suspended state and can be resumed using resume() method.
 * <p>
 * 2 public void stop() This method stops a thread completely.
 * <p>
 * 3 public void resume() This method resumes a thread which was suspended using suspend() method.
 * <p>
 * 4 public void wait() Causes the current thread to wait until another thread invokes the notify().
 * <p>
 * 5 public void notify() Wakes up a single thread that is waiting on this object's monitor.
 * <p>
 * Be aware that latest versions of Java has deprecated the usage of suspend( ), resume( ), and stop( ) methods and so
 * you need to use available alternatives.
 * 
 * @author Bart88
 *
 */
class RunnableDemo implements Runnable
{
	public Thread t;
	private String threadName;
	boolean suspended = false;

	RunnableDemo(String name)
	{
		threadName = name;
		System.out.println("Creating " + threadName);
	}

	public void run()
	{
		System.out.println("Running " + threadName);
		try
		{
			for (int i = 10; i > 0; i--)
			{
				System.out.println("Thread: " + threadName + ", " + i);
				// Let the thread sleep for a while.
				Thread.sleep(300);
				synchronized (this)
				{
					while (suspended)
					{
						wait();
					}
				}
			}
		}
		catch (InterruptedException e)
		{
			System.out.println("Thread " + threadName + " interrupted.");
		}
		System.out.println("Thread " + threadName + " exiting.");
	}

	public void start()
	{
		System.out.println("Starting " + threadName);
		if (t == null)
		{
			t = new Thread(this, threadName);
			t.start();
		}
	}

	void suspend()
	{
		suspended = true;
	}

	synchronized void resume()
	{
		suspended = false;
		notify();
	}
}

public class ControlThread
{
	public static void main(String args[])
	{

		RunnableDemo R1 = new RunnableDemo("Thread-1");
		R1.start();

		RunnableDemo R2 = new RunnableDemo("Thread-2");
		R2.start();

		try
		{
			Thread.sleep(1000);
			R1.suspend();
			System.out.println("Suspending First Thread");
			Thread.sleep(1000);
			R1.resume();
			System.out.println("Resuming First Thread");
			R2.suspend();
			System.out.println("Suspending thread Two");
			Thread.sleep(1000);
			R2.resume();
			System.out.println("Resuming thread Two");
		}
		catch (InterruptedException e)
		{
			System.out.println("Main thread Interrupted");
		}
		try
		{
			System.out.println("Waiting for threads to finish.");
			R1.t.join();
			R2.t.join();
		}
		catch (InterruptedException e)
		{
			System.out.println("Main thread Interrupted");
		}
		System.out.println("Main thread exiting.");
	}
}