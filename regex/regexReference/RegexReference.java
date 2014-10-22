package regexReference;

/**
 * http://msdn.microsoft.com/en-us/library/az24scfc%28v=vs.110%29.aspx#character_escapes
 * http://www.mkyong.com/regular-expressions/10-java-regular-expression-examples-you-should-know/
 * 
 * <pre>
 * When to use regex:
 * 1. To search through strings 
 * 2. To replace some data which appear many times in huge file
 * 3. To validate password, email address, user name,
 * 4. Validate image file extension
 * 5. 
 * 
 * </pre>
 *
 * <pre>
 * 	 \w Matches any word character.
 * 	 \W Matches any non-word character.
 * 	 \s Matches any white-space character.
 * 	 \S Matches any non-white-space character.
 * 	 \d Matches any decimal digit.
 * 	 \D Matches any character other than a decimal digit.
 * 	[A-Z] or [^A-Z]
 * 	[0-9] or [^0-9]
 * 	[a-z0-9_-]	     # Match characters and symbols in the list, a-z, 0-9 , underscore , hyphen
 * 	{3,15}  		 # Length at least 3 characters and maximum length of 15
 * 	^ .... $		 # Start and end of the string need to content this regex if not return false
 *  (?=.*\\d).{1}  # must contains one digit from 0-9
 *  
 * 
 *  [0-9a-z_-]{4,15}     # CAN we're looking for string with letters [a-z] digits [0-9] and can consist with -_
 * 	^[0-9a-z_-]{4,15}$ 	 # NEED TO ^ .... $ -> entire string need to has min 4 max 15 chars
 * 
 * 
 * </pre>
 * 
 * @author BartBien
 * @param args
 */
public class RegexReference
{
	//
	//

	// [ character_group ] - [ae] - "a" in "gray"
	// [^ character_group ] [^aei]
	// [ first - last ] [A-Z]
	// [0-9]

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
