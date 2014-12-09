package jvm.cpu;

public class AvailableProcessors
{
	static final int NCPU = Runtime.getRuntime().availableProcessors();

	public static void main(String[] args)
	{
		System.out.println(NCPU);
	}
}
