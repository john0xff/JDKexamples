package throwsEx;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ThrowsEx
{
//	public class className
//	{
//	   public void deposit(double amount) throws RemoteException
//	   {
//	      // Method implementation
//	      throw new RemoteException();
//	   }
//	   //Remainder of class definition
//	}
	
	@SuppressWarnings("resource")
	public static void streams() throws IOException
	{
		new ObjectInputStream(new InputStream()
		{
			
			@Override
			public int read() throws IOException
			{
				// TODO Auto-generated method stub
				return 0;
			}
		});
	}
}
