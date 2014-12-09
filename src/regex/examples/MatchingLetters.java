package regex.examples;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MatchingLetters
{

	public static void main(String[] args)
	{
		
		String regex = "[be]";
		Pattern pattern = Pattern.compile(regex);
		
		String charSecquence = "test beta version to enterprise";
		Matcher matcher = pattern.matcher(charSecquence);
		
		String tmp = null;
		int i = 0, j = 0;
		while (matcher.find())
		{
			System.out.println(matcher.group());
			tmp = matcher.group();
			
			if(tmp.equals("e"))
				i++;
			else if(tmp.equals("b"))
				j++;
		}
		System.out.println("e times -> " + i);
		System.out.println("b times -> " + j);
	}

}
