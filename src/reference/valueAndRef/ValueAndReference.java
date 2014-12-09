package reference.valueAndRef;


/**
 * 
 * http://stackoverflow.com/questions/40480/is-java-pass-by-reference-or-pass-by-value
 * 
 * 
 * 
 * @author BartBien
 *
 */
class Dog
{
	String dogsName;

	public Dog(String dogsName)
	{
		this.dogsName = dogsName;
	}

	public String getName()
	{
		return dogsName;
	}

	public void setName(String dogsName)
	{
		this.dogsName = dogsName;
	}
}

public class ValueAndReference
{

	public static void foo(Dog d)
	{
		boolean equalsMax = d.getName().equals("Max"); // true // valueAndRef.Dog@3b95a09c

		d = new Dog("Fifi"); // valueAndRef.Dog@6ae40994

		boolean equalsFifi = d.getName().equals("Fifi"); // true // valueAndRef.Dog@6ae40994
	}

	public static void fooTest()
	{
		Dog aDog = new Dog("Max"); // valueAndRef.Dog@3b95a09c

		foo(aDog);

		boolean equalsMax = aDog.getName().equals("Max"); // true // valueAndRef.Dog@3b95a09c

		aDog = new Dog("Fifi"); // valueAndRef.Dog@1a93a7ca

		boolean equalsFifi = aDog.getName().equals("Fifi"); // true // valueAndRef.Dog@1a93a7ca
	}

	public static void foo2(Dog d)
	{
		boolean equalsMax = d.getName().equals("Max"); // true // valueAndRef.Dog@3b95a09c

		d.setName("Fifi");

		boolean equalsFifi = d.getName().equals("Fifi"); // true // valueAndRef.Dog@6ae40994
	}

	public static void foo2Test()
	{
		Dog aDog = new Dog("Max"); // valueAndRef.Dog@3b95a09c

		foo2(aDog);

		// DIFFERENCE
		boolean equalsMax = aDog.getName().equals("Max"); // FALSE // valueAndRef.Dog@3b95a09c
		boolean equalsMax2 = aDog.getName().equals("Fifi");
	}

	public static void addSometing(int a)
	{
		a += 10;

		System.out.println(a);
	}

	public static int addSometing2(int a)
	{
		a += 10;

		System.out.println(a);

		return a;
	}

	public static void main(String[] args)
	{
		// fooTest(); // true everywhere

		// foo2Test(); // difference - false

		// int a = 10;
		// addSometing(a);
		// System.out.println(a);

		// int a = 10;
		// addSometing(a);
		// System.out.println(addSometing2(a));
		// System.out.println(a);
	}

}
