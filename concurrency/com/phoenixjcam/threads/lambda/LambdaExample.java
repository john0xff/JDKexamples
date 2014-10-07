package com.phoenixjcam.threads.lambda;

public class LambdaExample
{
	public static void main(String[] args)
	{
		new Thread(() -> System.out.println("lambda expression")).start();

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.println("ordinary runnable");
			}
		}).start();
	}
}
