package nestedClasses.inner;

// http://docs.oracle.com/javase/tutorial/java/javaOO/nested.html
class Test
{
	private int a;

	public Test()
	{
		a = 1;
		System.out.println(a);
	}
	
	class NestedClass
	{
		
		public NestedClass()
		{
			System.out.println(a);
		}
		
		class NestedClass2
		{
		
			public NestedClass2()
			{
				System.out.println(a);
			}
		}
	}
}

public class Snippet
{
	class Test2
	{
		private static final int a = 2;

		public Test2()
		{

			System.out.println(a);
		}

	}

	public static void main(String[] args)
	{
		class Test3
		{
			private int a;

			public Test3()
			{
				a = 3;
				System.out.println(a);
			}
		}

		

	}
}
