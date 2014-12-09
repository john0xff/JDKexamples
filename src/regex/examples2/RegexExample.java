package regex.examples2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExample
{
	public static void main(String[] args)
	{
		
		String input = "John was born in 2010";
		String regex = "[h]";
		Pattern pattern = Pattern.compile(regex);
		
		Matcher matcher = pattern.matcher(input);
		
		while (matcher.find())
		{
			System.out.println(matcher.group());
		}
	}

}
