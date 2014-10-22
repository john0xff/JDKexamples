package mkyoung.password;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator
{

	private Pattern pattern;
	private Matcher matcher;

	private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

	public PasswordValidator()
	{
		pattern = Pattern.compile(PASSWORD_PATTERN);
	}

	/**
	 * Validate password with regular expression
	 * 
	 * @param password
	 *            password for validation
	 * @return true valid password, false invalid password
	 */
	public boolean validate(final String password)
	{

		matcher = pattern.matcher(password);
		return matcher.matches();

	}

	public static void main(String... args)
	{

		for (int i = 0; i < args.length; i++)
		{
			if (new PasswordValidator().validate(args[i]))
				System.out.println("Passwd valid: " + args[i]);
			else
				System.out.println("Passwd invalid: " + args[i]);
		}

	}

}