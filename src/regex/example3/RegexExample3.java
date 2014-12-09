package regex.example3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExample3
{
	private final static String NEW_LINE = "\n";
	static File file = new File("regex/example3/data.txt");

	public static void writeStringToFile()
	{

		try
		{
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

			// structure of file - nm of line
			bufferedWriter.write("2" + NEW_LINE);

			bufferedWriter.write("test test" + NEW_LINE);
			bufferedWriter.write(" 222 222 333");
			bufferedWriter.flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String readStringFromFile()
	{
		String returnValue = null;

		try
		{
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

			try
			{
				int numberOfLines = Integer.parseInt(bufferedReader.readLine());

				StringBuilder stringBuilder = new StringBuilder();

				for (int i = 0; i < numberOfLines; i++)
				{
					stringBuilder.append(bufferedReader.readLine());
				}

				returnValue = stringBuilder.toString();
			}
			catch (NumberFormatException | IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
	}

	public static void findRegex(String readFile)
	{
		String regex = "[a-f]";
		Pattern pattern = Pattern.compile(regex);

		String input = readFile;
		Matcher matcher = pattern.matcher(input);

		int i = 0;
		while (matcher.find())
		{
			System.out.println(matcher.group());
			i++;
		}
		
		System.out.println("Iterations: " + i);
	}

	public static void main(String[] args)
	{
		//writeStringToFile();

		String readFile = readStringFromFile();

		System.out.println(readFile);

		findRegex(readFile);

	}

}
