import java.io.IOException;

public class AssertionTest2
{
	private static boolean checkInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static void main(String argv[]) throws IOException
	{

		String s1 = "2";
		assert checkInteger(s1);

	}

}