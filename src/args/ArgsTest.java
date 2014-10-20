package args;

public class ArgsTest
{
	// 1 2 3 4 5 bla ble bla :)
	// should display -> Arr length 9 and all above data
	// public static void main(String[] args)
	public static void main(String... args)
	{
		System.out.println("Arr length " + args.length);

		for (String string : args)
		{
			System.out.println(string);
		}
	}
}
