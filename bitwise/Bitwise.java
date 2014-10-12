public class Bitwise
{

	public static void bitwise()
	{
		int a = 60; /* 60 = 0011 1100 */
		int b = 13; /* 13 = 0000 1101 */
		int c = 0;

		c = a & b; /* 12 = 0000 1100 */
		System.out.println("a & b = " + c);

		c = a | b; /* 61 = 0011 1101 */
		System.out.println("a | b = " + c);

		c = a ^ b; /* 49 = 0011 0001 */
		System.out.println("a ^ b = " + c);

		c = ~a; /*-61 = 1100 0011 */
		System.out.println("~a = " + c);

		c = a << 2; /* 240 = 1111 0000 */
		System.out.println("a << 2 = " + c);

		c = a >> 2; /* 215 = 1111 */
		System.out.println("a >> 2  = " + c);

		c = a >>> 2; /* 215 = 0000 1111 */
		System.out.println("a >>> 2 = " + c);
	}

	public static void main(String args[])
	{
		int a = 128; // 0000 0010
		int b = 4; // 0000 0100
		int c = 0;
		
		//c = 4 << 2; // 0001 0000
		//c = 4 >> 2; // 0000 0001
		
		//c = a >>> 1;
		c = a >>> 4;
		System.out.println("c = a & b; " + c);
	}
}