package reflection.getMethods;

import gui.swing.SWING;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.Map;

public class GetAnnotations
{
	static Method[] methods = Thread.class.getMethods();

	public static void main(String... args)
	{
		for (Annotation annotation : Runnable.class.getAnnotations())
		{
			System.out.println("annotation = " + annotation.annotationType());
		}
		
	}
}
