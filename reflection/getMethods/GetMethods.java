package getMethods;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.Map;

import swing.SWING;

public class GetMethods
{
	static Method[] methods = Thread.class.getMethods();

	public static void main(String... args)
	{
		for (Method method : Thread.class.getMethods())
		{
			System.out.println("method = " + method.getName());
		}
		
		System.out.println(int.class);
		
		System.out.println(double.class);
	
		System.out.println(String.class);
		
		System.out.println(SWING.class);
		
		System.out.println(Integer.class);
		
		System.out.println(ServerSocket.class);
	}
}
