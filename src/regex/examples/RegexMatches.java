package regex.examples;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://docs.oracle.com/javase/tutorial/essential/regex/index.html
 * http://www.tutorialspoint.com/java/java_regular_expressions.htm
 * http://www.vogella.com/tutorials/JavaRegularExpressions/article.html
 * 
 * http://www.phoenixjcam.com/18-regex
 * 
 * @author BartBien
 *
 */
public class RegexMatches
{
	public static void main(String args[])
	{

		// String to be scanned to find the pattern.
		String line = "This order was placed for QT3000! OK?";
		String pattern = "(.*)(\\d+)(.*)";

		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = r.matcher(line);
		if (m.find())
		{
			System.out.println("Found value: " + m.group(0));
			System.out.println("Found value: " + m.group(1));
			System.out.println("Found value: " + m.group(3));
			System.out.println("Found value: " + m.group(2));
		}
		else
		{
			System.out.println("NO MATCH");
		}
	}
}