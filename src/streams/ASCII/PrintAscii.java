package streams.ASCII;

class PrintAscii
{

	public static void main(String[] args)
	{
		System.out.println("List of ASCII Characters are given below: ");

		for (int i = 0; i <= 255; i++)
		{
			System.out.format("%1$-5d", i);
			System.out.format("%1$-2c", (char) i);
			System.out.println();
		}
	}
}
