package com.phoenixjcam.threads.synchroniz.practice;

class ThreadFactory
{
	private Thread thread;
	
	public ThreadFactory(String threadName)
	{
		thread = new Thread(
				()->
				{
					for (int i = 0; i < 100; i++)
					{
						printThreadName(i);
					}
				}
				, threadName);
		thread.start();
	}
	
	private void printThreadName(int i)
	{
		System.out.println(this.thread.getName() + " " + i);
		try
		{
			Thread.sleep(3000);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

public class UnsynchronizedThreadTests
{

	public static void main(String[] args) throws InterruptedException
	{
//		ThreadFactory thFac = new ThreadFactory("A");
//		ThreadFactory thFac2 = new ThreadFactory("B");
//		ThreadFactory thFac3 = new ThreadFactory("C");
//
		
		int start = 65;
		int end = 125;
		int size = end - start;
		char[] charArr = new char[size];
		
		for (int i = 0; i < size; i++)
		{
			int codePoint = i + 65; 
			charArr[i] = (char)codePoint;
		}

		for (int i = 0; i < 5; i++)
		{
			new ThreadFactory(String.valueOf(i) + " thread - ");
		}
		
		
//		for (int i = 0; i < 100; i++)
//		{
//			System.out.println("main " + i);
//			Thread.sleep(1000);
//		}
	}
}
