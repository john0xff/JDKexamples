package streams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.imageio.stream.FileImageInputStream;

public class StreamsExample
{

	public static void main(String[] args)
	{
		File file = new File("uncommit/streams/test.txt");

		FileOutputStream fileOutputStream = null;
		FileInputStream fileInputStream;

		BufferedReader in;
		BufferedWriter out;

		InputStreamReader inputStreamReader;
		OutputStreamWriter outputStreamWriter;

		DataOutputStream dataOutputStream;
		DataInputStream dataInputStream;

		DataInput dataInput;
		DataOutput dataOutput;

		try
		{
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(""), "UTF8"));
		}
		catch (UnsupportedEncodingException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try
		{
			fileOutputStream = new FileOutputStream(file);

			byte[] byteArray = "aa".getBytes();
			fileOutputStream.write(byteArray);
			fileOutputStream.flush();

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
		finally
		{
			try
			{
				fileOutputStream.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
