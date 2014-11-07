package getMethods;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.Map;

import swing.SWING;

public class GetMethodsAndParam
{
	static Method[] methods = Thread.class.getMethods();

	public static void main(String... args)
	{
		for (Method method : ServerSocket.class.getMethods())
		{
			System.out.println("method = " + method.getName());
			System.out.println("number of param = " + method.getParameterCount());
		}
		
	}
}
