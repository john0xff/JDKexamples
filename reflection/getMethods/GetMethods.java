package getMethods;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;

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
	}
}
