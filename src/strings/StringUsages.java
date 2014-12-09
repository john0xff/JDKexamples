package strings;

import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.StringJoiner;


public class StringUsages
{
	public static void main(String[] args)
	{
	    /** The value is used for character storage. */
	    // private final char value[];
		// String it's just array of chars with couple of convenient methods
		// It is possible to append something to string with join method or += operator.
		
		// --------------------
		String string;
		StringJoiner stringJoiner;
		
		// --------------------
		StringBuilder stringBuilder;
		
		// -------------------- same as StringBuilder but with synchronized methods
		StringBuffer stringBuffer;
		
		
		concatinate();
		concatinate2();
		stringJoin();
		stringBuilder();
	}
	
	public static void concatinate()
	{
		String strTmp = "test1";
		strTmp += "test2";
		System.out.println("Plain string appended via += -> " + strTmp);
	}
	
	public static void concatinate2()
	{
		String strTmp = new String();
		strTmp = "test1";
		strTmp.concat("test2");
		System.out.println("concat(\"test2\") -> " + strTmp);
		System.out.println("concat(\"test2\") -> " + "test1".concat("test2"));
	}
	
	public static void stringJoin()
	{
		String strToJoin1 = "test1";
		String strToJoin2 = "test2";
		String strJoined = String.join("", strToJoin1, strToJoin2);
		
		System.out.println("strJoined -> " + strJoined);
	}
	
	public static void stringBuilder()
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("test1");
		stringBuilder.append("test2");
		
		System.out.println("StringBuilder.append -> " + stringBuilder);
	}
}
