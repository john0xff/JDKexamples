package net.sending.objects;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client
{
	private Socket socket;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	
	private PlayerEnvelope playerEnvelope;

	public Client()
	{
		this.playerEnvelope = new PlayerEnvelope("john", new Point(200, 100));
		
		try
		{
			this.socket = new Socket("127.0.0.1", 9002);

			this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
			this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		for (int i = 0; i < 5; i++)
		{
			char name = (char) (65 + i);
			
			this.playerEnvelope = new PlayerEnvelope(String.valueOf(name), new Point(200 + i, 100 + i));
			
			try
			{
				this.objectOutputStream.writeObject(this.playerEnvelope);

				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args)
	{
		new Client();
	}

}
