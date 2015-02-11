package regex.tabOrNewLine;

public class RegexSample
{

	public static void main(String[] args)
	{
		replaceAll();
	}

	public static void replaceAll(){
		String test = "test \n test";
		System.out.println(test);

		System.out.println("------------------------");

		String newOne = test.replaceAll("\n", "aaa");

		System.out.println(newOne);
	}

	public static void replaceAll2(){
		String str = "This is a String to use as an example to present raplaceAll";

		// replace all occurrences of 'a' with '@'
		String newStr = str.replaceAll("a", "@");
		System.out.println(newStr);

		// replace all occurrences of 'e' with '3'
		newStr = newStr.replaceAll("e", "3");
		System.out.println(newStr);

		// replace all occurrences of 't' with 'T'
		newStr = newStr.replaceAll("t", "T");
		System.out.println(newStr);

		// remove all occurrences of 'o'
		newStr = newStr.replaceAll("o", "");
		System.out.println(newStr);

		// replace all occurrences of 't' with 'That'
		newStr = newStr.replaceAll("T", "That");
		System.out.println(newStr);
	}


}
