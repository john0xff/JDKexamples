package regex.mkyoung.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator
{

	private Pattern pattern;
	private Matcher matcher;

	private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";

	public UsernameValidator()
	{
		pattern = Pattern.compile(USERNAME_PATTERN);
	}

	/**
	 * Validate username with regular expression
	 * 
	 * @param username
	 *            username for validation
	 * @return true valid username, false invalid username
	 */
	public boolean validate(final String username)
	{

		matcher = pattern.matcher(username);
		return matcher.matches();
	}

	public static void main(String... args)
	{
		if (args.length > 0)
		{
			if (new UsernameValidator().validate(args[0]))
				System.out.println("Valid -> " + args[0]);
			else
				System.out.println("Invalid -> " + args[0]);
		}
		else
			System.out.println("Give an argument at the begining");
	}
}