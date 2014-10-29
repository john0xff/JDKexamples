package bitshifts;

public class Bitshifts
{
	public static void main(String[] args)
	{
		int eight = 8;
		int newOne = eight >>> 3;
		System.out.println(newOne);
		
		System.out.println(16 << 1);
		System.out.println(16 >> 1);
		System.out.println(16 >>> 4);

		System.out.println(10 >>> 2); // 0000 1010 -> 0000 0101 = 10 -> 5
		System.out.println(10 >> 2);
		
		System.out.println(10 >> 2); // 0010 0000 
		
	}
}
