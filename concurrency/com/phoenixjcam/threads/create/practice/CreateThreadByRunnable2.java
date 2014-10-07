package com.phoenixjcam.threads.create.practice;

class ThreadExample implements Runnable
{
	private Thread thread;

	public ThreadExample(String threadName)
	{
		thread = new Thread(this, threadName);
		thread.start();
	}

	public void run()
	{
		for (int i = 10; i > 0; i--)
		{
			System.out.println(this.thread.getName() + " counting down " + i);

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

	}
}

public class CreateThreadByRunnable2
{
	private void runnableExample()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				for (int i = 0; i < 100; i++)
				{
					System.out.println("count " + i);

					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				for (int i = 0; i < 100; i++)
				{
					System.out.println("king " + i);

					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static void main(String[] args)
	{
//		new Thread(() -> System.out.println("lambda expression")).start();
//		
//		new Thread(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				System.out.println("ordinary runnable");
//			}
//		}).start();

		ThreadExample thExmp = new ThreadExample("master");
		
		
	}
}
