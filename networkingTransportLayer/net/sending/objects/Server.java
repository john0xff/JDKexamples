package net.sending.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server
{
	private ServerSocket serverSocket;
	private Socket socket;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	// private sendingObjects.client.PlayerEnvelope playerEnvelope;
	private PlayerEnvelope playerEnvelope;

	public Server()
	{
		// this.playerEnvelope = new sendingObjects.client.PlayerEnvelope(null, null);
		this.playerEnvelope = new PlayerEnvelope(null, null);
		try
		{
			this.serverSocket = new ServerSocket(9002);
			this.socket = this.serverSocket.accept();

			this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
			this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		while (true)
		{
			try
			{
				// String msg = this.objectInputStream.readObject().toString();

				playerEnvelope = (PlayerEnvelope) this.objectInputStream.readObject();
				System.out.println(playerEnvelope.getName());
				System.out.println(playerEnvelope.getPosition());
			}
			catch (SocketException e)
			{
				break;
			}
			catch (ClassNotFoundException | IOException e)
			{
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args)
	{
		new Server();
	}

}
