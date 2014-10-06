package com.phoenixjcam.threads.id;

public class IdThread extends Object implements Runnable
{
	private ThreadID var;

	public IdThread(ThreadID v)
	{
		this.var = v;
	}

	public void run()
	{
		try
		{
			print("var getThreadID =" + var.getThreadID());
			Thread.sleep(2000);
			print("var getThreadID =" + var.getThreadID());
		}
		catch (InterruptedException x)
		{
		}
	}

	private static void print(String msg)
	{
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}

	public static void main(String[] args)
	{
		ThreadID tid = new ThreadID();
		IdThread shared = new IdThread(tid);

		try
		{
			Thread threadA = new Thread(shared, "threadA");
			threadA.start();

			Thread.sleep(500);

			Thread threadB = new Thread(shared, "threadB");
			threadB.start();

			Thread.sleep(500);

			Thread threadC = new Thread(shared, "threadC");
			threadC.start();
		}
		catch (InterruptedException x)
		{
		}
	}
}

class ThreadID extends ThreadLocal
{
	private int nextID;

	public ThreadID()
	{
		nextID = 10001;
	}

	private synchronized Integer getNewID()
	{
		Integer id = new Integer(nextID);
		nextID++;
		return id;
	}

	protected Object initialValue()
	{
		print("in initialValue()");
		return getNewID();
	}

	public int getThreadID()
	{
		Integer id = (Integer) get();
		return id.intValue();
	}

	private static void print(String msg)
	{
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}
}