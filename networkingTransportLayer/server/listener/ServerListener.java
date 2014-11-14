package server.listener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <pre>
 * Listen for a connection from client.
 * In this case client is the browser like chrome or firefox.
 * 
 * Only to connect with request without any response.
 * 
 * Port set in router NAT (9002) to local server on public IP (DDSN) with domain registered on no-ip service. 
 * To connect (server needs to run before request from browser ;)  )
 * http://phoenixjcam.no-ip.biz:9002
 * 
 * @author bart400
 *
 */
public class ServerListener
{
	private ServerSocket serverSocket;
	private Socket socket;

	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	private int port = 9002;

	public ServerListener()
	{
		try
		{
			this.serverSocket = new ServerSocket(port);
			this.socket = this.serverSocket.accept();
			// this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
			// this.inputStream = new ObjectInputStream(this.socket.getInputStream());

			System.out.println("connected");

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		while (true)
		{
			// try
			// {
			// String msg = this.inputStream.readObject().toString();
			// System.out.println(msg);
			//
			//
			// }
			// catch (ClassNotFoundException e)
			// {
			// e.printStackTrace();
			// }
			// catch (IOException e)
			// {
			// e.printStackTrace();
			// }

			System.out.println("ss");

			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args)
	{
		new ServerListener();
	}

}
