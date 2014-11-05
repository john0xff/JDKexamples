import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.StringJoiner;


public class StringUsages
{
	public static void main(String[] args)
	{
		// --------------------
		String string;
		StringJoiner stringJoiner;
		
		String strTmp = "test1";
		strTmp += "test2";
		System.out.println("Plain string appended via += -> " + strTmp);
		
		boolean bool = strTmp.matches("test1test2");
		
		String strToJoin1 = "test1";
		String strToJoin2 = "test2";
		String strJoined = String.join("", strToJoin1, strToJoin2);
		
		System.out.println("strJoined -> " + strJoined);
		
		// --------------------
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("test1");
		stringBuilder.append("test2");
		
		System.out.println("StringBuilder.append -> " + strJoined);
		
		
		// -------------------- same as StringBuilder but with synchronized methods
		StringBuffer stringBuffer;
	}
}
