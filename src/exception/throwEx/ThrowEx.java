package exception.throwEx;

import java.util.EmptyStackException;

/**
 * http://docs.oracle.com/javase/tutorial/essential/exceptions/throwing.html
 * 
 * @author BartBien
 *
 */
public class ThrowEx
{
	public static void main(String[] args)
	{
		pop();
	}
	
	

	private static int size = 0;

	public static Object pop()
	{
		Object obj;

		if (size == 0)
		{
			throw new EmptyStackException();
		}

		obj = objectAt(size - 1);
		setObjectAt(size - 1, null);
		size--;
		return obj;
	}

	private static void setObjectAt(int i, Object object)
	{
		// TODO Auto-generated method stub

	}

	private static Object objectAt(int i)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
