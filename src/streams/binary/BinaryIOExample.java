package streams.binary;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Shows how easy to deal with binary data. <br>
 * Save byte array to file. <br>
 * Load byte array from the same file. <br>
 * 
 * @author Bart88
 *
 */
public class BinaryIOExample
{
	private static boolean closeObject(Closeable object)
	{
		try
		{
			object.close();

			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}

	private static boolean saveCharacters(String path, char[] data)
	{
		FileOutputStream fileStream = null;
		DataOutputStream dataStream = null;

		try
		{
			fileStream = new FileOutputStream(path);
			dataStream = new DataOutputStream(fileStream);

			// binary file structure
			// signed 32 bits - number of charts
			// number_of_charts * 2 bits - charactes

			dataStream.writeInt(data.length); // write number of characters

			for (char el : data)
				// foreach character
				dataStream.writeChar(el); // write character value

			return true;
		}
		catch (IOException e)
		{
			return false;
		}
		finally
		{
			if (dataStream != null)
				closeObject(dataStream);

			if (fileStream != null)
				closeObject(fileStream);
		}
	}

	private static char[] loadCharacters(String path)
	{
		FileInputStream fileStream = null;
		DataInputStream dataStream = null;

		try
		{
			fileStream = new FileInputStream(path);
			
			//System.out.print("...");
			
			dataStream = new DataInputStream(fileStream);

			// binary file structure
			// signed 32 bits - number of charts
			// number_of_charts * 2 bits - charactes

			int length = dataStream.readInt(); // read number of characters
			char[] data = new char[length];

			for (int i = 0; i < length; ++i)
				// foreach character
				data[i] = dataStream.readChar(); // read character value

			return data;
		}
		catch (IOException e)
		{
			return null;
		}
		finally
		{
			if (dataStream != null)
				closeObject(dataStream);

			if (fileStream != null)
				closeObject(fileStream);
		}
	}

	public static void main(String[] args)
	{
		{
			char[] buffer = new char[100];

			for (int i = 0; i < buffer.length; i++)
				buffer[i] = (char) ('a' + i);

			saveCharacters("streams/com/phoenixjcam/binary/data.dat", buffer);
		}

		{
			char[] buffer = loadCharacters("streams/com/phoenixjcam/binary/data.dat");

			for (int i = 0; i < buffer.length; i++)
				System.out.print(buffer[i]);
		}
	}

}
