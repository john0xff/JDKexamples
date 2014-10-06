package com.phoenixjcam.threads.synchroniz;

class SynchPrintDemo
{
	public void printCount()
	{
		try
		{
			for (int i = 5; i > 0; i--)
			{
				System.out.println("Counter   ---   " + i);
			}
		}
		catch (Exception e)
		{
			System.out.println("Thread  interrupted.");
		}
	}

}

class SynchThreadDemo extends Thread
{
	private Thread t;
	private String threadName;
	SynchPrintDemo PD;

	SynchThreadDemo(String name, SynchPrintDemo pd)
	{
		threadName = name;
		PD = pd;
	}

	public void run()
	{
		synchronized (PD)
		{
			PD.printCount();
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

}

public class SynchronizedThread
{
	public static void main(String args[])
	{
		System.out.println("SynchronizedThread");
		System.out.println();
		
		SynchPrintDemo PD = new SynchPrintDemo();

		SynchThreadDemo T1 = new SynchThreadDemo("Thread - 1 ", PD);
		SynchThreadDemo T2 = new SynchThreadDemo("Thread - 2 ", PD);

		T1.start();
		T2.start();

		// wait for threads to end
		try
		{
			T1.join();
			T2.join();
		}
		catch (Exception e)
		{
			System.out.println("Interrupted");
		}
	}
}