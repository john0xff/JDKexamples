package streams.bufferedWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BuffWriterExample
{
	public static void main(String... args)
	{
		File file = new File("streams/bufferedWriter/bufferedWriter.txt");
		BufferedWriter bufferedWriter;
		
		try
		{
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			
			// structure of file - number of rows
			bufferedWriter.write("5\n");
			
			for (int i = 1; i < 5; i++)
			{
				bufferedWriter.write("line no " + i + " test test test " + "\n");
			}
			
			bufferedWriter.flush();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
