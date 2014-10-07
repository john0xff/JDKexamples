package com.phoenixjcam.threads.synchroniz.practice;

class UnsynchPrinter
{
	public void printText(String threadName)
	{
		try
		{
			for (int i = 0; i < 5; i++)
			{
				System.out.println(threadName + " " + i);
				Thread.sleep(1000);
			}
		}
		catch (Exception e)
		{
			System.out.println("Interrupt exception");
			e.printStackTrace();
		}
	}
}

class UnsynchThreadFact
{
	private Thread thread;

	public UnsynchThreadFact(String threadName, Printer printer)
	{
		thread = new Thread(() ->
		{
			printer.printText(threadName);
			
		}, threadName);
	}

	public Thread getThread()
	{
		return this.thread;
	}
}

public class UnsynchronizedThread2
{
	public static void main(String[] args)
	{
		Printer printer = new Printer();
		
		new UnsynchThreadFact("king", printer).getThread().start();
		new UnsynchThreadFact("master", printer).getThread().start();
	}
}
