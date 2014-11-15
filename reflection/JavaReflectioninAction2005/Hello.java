package JavaReflectioninAction2005;

public class Hello
{
	public void printName()
	{
		System.out.println(this.getClass().getName());
	}
	
	public static void main(String[] args)
	{
		// (new Hello()).printName();
		
		Hello x = new Hello();
		x.printName();
	}
}
