package apache.client.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 
 * http://phoenixjcam.no-ip.biz:80/index.html
 * 
 * @author BartBien
 *
 */
public class ApacheClientBrowser
{
	private int apachePort = 80;
	private int tomcatPort = 8081;

	private String host = "phoenixjcam.no-ip.biz";

	private Socket socket;

	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	public ApacheClientBrowser()
	{
		try
		{
			this.socket = new Socket(host, apachePort);

			PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

			while(bufferedReader.ready())
			{
				System.out.println(bufferedReader.readLine());	
			}
			
			
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{

		new ApacheClientBrowser();
	}
}
