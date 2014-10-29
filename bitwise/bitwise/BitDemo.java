package bitwise;
class BitDemo
{
	public static void main(String[] args)
	{
		int bitmask = 0x000F;
		int val = 0x2222;
		// prints "2"
		System.out.println(val & bitmask); // 2
		
		System.out.println(17 & 15); // 1
		System.out.println(17 | 15); // 31
		
		
		// hexadecimal
		int test = 0x001F; // 31
		System.out.println(test);
				
		int seventeen = 0x0011;
		int fifteen = 0x000F;
		
		System.out.println(seventeen & fifteen); // 1
		System.out.println(seventeen | fifteen); // 31
		
		int seventeen2 = 0x0011;
		int five = 0x0005;
		System.out.println(seventeen2 ^ five); // 20
	}
}