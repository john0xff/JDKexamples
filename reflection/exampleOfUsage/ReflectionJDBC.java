package exampleOfUsage;
public class ReflectionJDBC
{

	public static void main(String[] args)
	{
		Class thread;
		
		try
		{
			thread = Class.forName("java.lang.Thread");
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//thread.getClass().

	}

}
