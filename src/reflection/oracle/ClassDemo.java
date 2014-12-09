package reflection.oracle;

import java.lang.reflect.Field;

public class ClassDemo
{

	public static void main(String[] args)
	{

		try
		{
			Class cls = Class.forName("java.awt.Label");
			System.out.println("Fields =");

			// returns the array of Field objects representing the public fields
			Field f[] = cls.getFields();
			for (int i = 0; i < f.length; i++)
			{
				System.out.println(f[i]);
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e);
		}
	}
}