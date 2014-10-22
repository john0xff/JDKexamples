package examples;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest2
{

	public static void main(String[] args)
	{
		String input = "3a_-D";

		//String regex = "((?=.*[A-Z])(?=.*[a-z])(?=.*[2-5])(?=.*[$#!])).{4}";
		String regex = "^\\w";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(input);

		while (matcher.find())
		{
			System.out.println(matcher.group());
		}
	}

}
