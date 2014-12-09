package exception.throwEx;

import java.io.IOException;
import java.util.ArrayList;

public class MultiCatchWithThrow
{
	@SuppressWarnings("finally")
	public static void main(String... args)
	{
		try
		{
			int[] a =
			{ 1, 2, 3 };
			int b = a[4];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			throw new NullPointerException();
		}
	}
}
