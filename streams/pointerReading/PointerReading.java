package pointerReading;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PointerReading
{
	// from 1 to 10 (block of data)
	public static void main(String[] args)
	{
		File file = new File("streams/pointerReading/reader.txt");

		FileInputStream fileInputStream = null;
		try
		{
			fileInputStream = new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		DataInputStream dataInputStream = new DataInputStream(fileInputStream);

		try
		{
			int size = dataInputStream.available();
			System.out.println(size); // print length of file
			byte[] buffer = new byte[100];

			for (int i = 0; i < size; i += 5)
			{
				dataInputStream.read(buffer, i, 5);

				System.out.println((char) buffer[i]); // print first letter of next block of data
			}

			// print ASCII
			for (int i = 0; i < size; i++)
			{
				System.out.println((char) buffer[i]);
			}
			
			char[] charBuffer = new char[100];
			
			// convert byte to char
			for (int i = 0; i < size; i++)
			{
				charBuffer[i] = (char)buffer[i];
			}

			// build string
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(charBuffer);

			System.out.println(stringBuilder);

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
