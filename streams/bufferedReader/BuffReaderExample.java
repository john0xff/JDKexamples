package bufferedReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BuffReaderExample
{
	public static void main(String... args)
	{
		File file = new File("streams/bufferedReader/buffReader.txt");
		BufferedReader bufferedReader;

		try
		{
			bufferedReader = new BufferedReader(new FileReader(file));
			
			// structure of file - first line will be telling about number of rows
			int rows =  Integer.parseInt(bufferedReader.readLine());

			System.out.println("Nm of rows = " + rows);
			
			for (int i = 0; i < rows; i++)
			{
				System.out.println(bufferedReader.readLine());
			}
			
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
