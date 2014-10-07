package com.phoenixjcam.threads.create.practice;

public class CreateThreadByExtends2
{

	public static void main(String[] args)
	{
		System.out.println("Countdown");

		ThreadFrame threadFrame = new ThreadFrame("masterTh 1");
		threadFrame.start();
		
		ThreadFrame threadFrame2 = new ThreadFrame("kingTh   2");
		threadFrame2.start();
		
		ThreadFrame threadFrame3 = new ThreadFrame("testTh   3");
		threadFrame3.start();
	}
}

class ThreadFrame extends Thread
{
	private Thread thread;
	private String threadName;

	public ThreadFrame(String threadName)
	{
		this.threadName = threadName;
		
		if (thread == null)
		{
			thread = new Thread(this, threadName);
		}
	}

	@Override
	public void run()
	{
		for (int i = 10; i > 0; i--)
		{
			String thName = this.thread.getName();

			System.out.println(thName + " count down " + i);

			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println(this.thread.getName() + " I've done for today.");
	}

//	@Override
//	public synchronized void start()
//	{
//		if (thread == null)
//		{
//			thread = new Thread(this, threadName);
//
//			this.thread.start();
//		}
//	}
}