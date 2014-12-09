package reference.passPrimitives;

public class PassPrimitives
{
	public static void changeNumb(int tmp)
	{
		tmp = 5;
	}
	
	public static void main(String[] args)
	{
		int numb = 10;
		
		System.out.println(numb);
		
		changeNumb(numb);
		
		System.out.println(numb);
	}
}
