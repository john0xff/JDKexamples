package com.phoenixjcam.threads.synchroniz.practice;

class Printer
{
	public void printText(String threadName)
	{
		try
		{
			for (int i = 0; i < 5; i++)
			{
				System.out.println(threadName + " " + i);
				//Thread.sleep(100);
			}
		}
		catch (Exception e)
		{
			System.out.println("Interrupt exception");
			e.printStackTrace();
		}
	}
}

class ThreadFact
{
	private Thread thread;

	public ThreadFact(String threadName, Printer printer)
	{
		thread = new Thread(() ->
		{
			 synchronized(printer)
			 {
				 printer.printText(threadName);
			 }

		}, threadName);
	}

	public Thread getThread()
	{
		return this.thread;
	}
}

public class SynchronizedThread2
{
	public static void main(String[] args)
	{
		Printer printer = new Printer();
		
		new ThreadFact("king", printer).getThread().start();
		new ThreadFact("master", printer).getThread().start();
	}
}
