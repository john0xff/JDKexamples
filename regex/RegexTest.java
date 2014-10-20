import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest
{

	public static void main(String[] args)
	{
		// words
		Pattern pattern1 = Pattern.compile("\\w");
		Pattern pattern2 = Pattern.compile("\\w+");
		Pattern pattern3 = Pattern.compile("\\w*");
		
		// digits
		Pattern pattern4 = Pattern.compile("\\d");
		Pattern pattern5 = Pattern.compile("\\d+");
		Pattern pattern6 = Pattern.compile("\\d*");
		
		String str = "test test2 test3";
		Matcher matcher = pattern1.matcher(str);
		
		while (matcher.find())
		{
			System.out.println(matcher.group());
		}
	}

}
