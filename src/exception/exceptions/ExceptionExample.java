package exception.exceptions;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ExceptionExample
{

	public static void main(String[] args)
	{
		// --------------------------------------------------
		int[] arrInt = new int[2];
		arrInt[0] = 1;

		try
		{
			int test = arrInt[3];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			System.out.println(e);
		}
		catch (Exception e1)
		{
			System.out.println(e1);
		}

		// --------------------------------------------------
		try
		{
			ObjectInputStream objectInputStream = null;
			objectInputStream.readObject().toString();
		}
		catch (ClassNotFoundException | IOException e)
		{
			System.out.println(e);
		}
		catch (Exception e1)
		{
			System.out.println(e1);
		}

		// --------------------------------------------------
		try
		{
			ObjectInputStream objectInputStream = null;
			objectInputStream.readObject().toString();
		}
		catch (ClassNotFoundException | IOException | NullPointerException e)
		{
			System.out.println(e);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		catch (Throwable e1)
		{
			System.out.println(e1);
		}
		// --------------------------------------------------
	}
}
