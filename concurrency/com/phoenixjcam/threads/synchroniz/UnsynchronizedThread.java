package com.phoenixjcam.threads.synchroniz;

class UnsynchPrintDemo
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

class UnsynchThreadDemo extends Thread
{
	private Thread t;
	private String threadName;
	UnsynchPrintDemo PD;

	UnsynchThreadDemo(String name, UnsynchPrintDemo pd)
	{
		threadName = name;
		PD = pd;
	}

	public void run()
	{
		PD.printCount();
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

public class UnsynchronizedThread
{
	public static void main(String args[])
	{
		System.out.println("UnsynchronizedThread");
		System.out.println();
		
		UnsynchPrintDemo PD = new UnsynchPrintDemo();

		UnsynchThreadDemo T1 = new UnsynchThreadDemo("Thread - 1 ", PD);
		UnsynchThreadDemo T2 = new UnsynchThreadDemo("Thread - 2 ", PD);

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