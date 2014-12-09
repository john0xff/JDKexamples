package exception.throwEx;

import java.util.ArrayList;

public class NullPointerEx
{
	@SuppressWarnings("finally")
	public static void main(String... args)
	{
		try
		{
			ArrayList<String> c = null;
			c.get(2);
		}
		catch (NullPointerException e)
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

