package networking.sockets.allegro.msg;

import java.net.*;
import java.io.*;

public class ApacheGetHTMLwithSocket
{

	@SuppressWarnings("resource")
	public static void main(String[] args)
	{

		int port = 80;

		try
		{
			URL u = new URL("http://allegro.pl/index.php");

			if (u.getPort() != -1)
				port = u.getPort();

			if (!(u.getProtocol().equalsIgnoreCase("http")))
			{
				System.err.println("something else then http");

			}

			Socket s = new Socket(u.getHost(), port);
			OutputStream theOutput = s.getOutputStream();
			// no auto-flushing
			PrintWriter pw = new PrintWriter(theOutput, false);
			// native line endings are uncertain so add them manually
			pw.print("GET " + u.getFile() + " HTTP/1.0\r\n");
			pw.print("Accept: text/plain, text/html, text/*\r\n");
			pw.print("\r\n");
			pw.flush();
			
			InputStream in = s.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			
			// int c;
			// while ((c = br.read()) != -1)
			// {
			// System.out.print((char) c);
			// }
			
			File file = new File("networkingTransportLayer/apache/client/url2/index.html");

			BufferedWriter bufferedWriter;

			bufferedWriter = new BufferedWriter(new FileWriter(file));
	
			String line;
			while ((line = br.readLine()) != null)
			{
				System.out.println(line);
				bufferedWriter.write(line + "\n");
			}
			bufferedWriter.flush();

		}
		catch (IOException ex)
		{
			System.err.println(ex);
		}

	}

}