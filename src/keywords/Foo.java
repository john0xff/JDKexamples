package keywords;
public class Foo
{
	public static void main(String[] args)
	{
		Baz.testAsserts();
		// Will execute after Baz is initialized.
		
	
		
	}

//	public boolean acquireFoo(int id)
//	{
//		Foo result = null;
//		
//		if (id > 50)
//		{
//			result = fooService.read(id);
//		}
//		else
//		{
//			result = new Foo(id);
//		}
//		
//		assert result != null;
//
//		return result;
//	}
}

class Bar
{
	static
	{
		Baz.testAsserts();
		// Will execute before Baz is initialized!
	}
}

class Baz extends Bar
{
	static void testAsserts()
	{
		boolean enabled = false;
		assert enabled = true;
		System.out.println("Asserts " + (enabled ? "enabled" : "disabled"));
	}
}