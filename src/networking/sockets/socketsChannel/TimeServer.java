package networking.sockets.socketsChannel;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

public class TimeServer
{

	// We can't use the normal daytime port (unless we're running as root,
	// which is unlikely), so we use this one instead
	private static int PORT = 9005;

	// The port we'll actually use
	private static int port = PORT;

	// Charset and encoder for US-ASCII
	private static Charset charset = Charset.forName("US-ASCII");
	private static CharsetEncoder encoder = charset.newEncoder();

	// Direct byte buffer for writing
	private static ByteBuffer dbuf = ByteBuffer.allocateDirect(1024);

	// Open and bind the server-socket channel
	//
	private static ServerSocketChannel setup() throws IOException
	{
		ServerSocketChannel ssc = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(), port);
		ssc.socket().bind(isa);
		return ssc;

	}

	// Service the next request to come in on the given channel
	//
	private static void serve(ServerSocketChannel ssc) throws IOException
	{
		java.nio.channels.SocketChannel sc = ssc.accept();
		try
		{
			String now = new Date().toString();
			sc.write(encoder.encode(CharBuffer.wrap(now + "\r\n")));
			System.out.println(sc.socket().getInetAddress() + " : " + now);
			sc.close();
		}
		finally
		{
			// Make sure we close the channel (and hence the socket)
			sc.close();
		}

	}

	public static void main(String[] args) throws IOException
	{
//		if (args.length > 1)
//		{
//			System.err.println("Usage: java TimeServer [port]");
//			return;
//		}
//
//
//		// If the first argument is a string of digits then we take that
//		// to be the port number
//		if ((args.length == 1) && Pattern.matches("[0-9]+", args[0]))
//			port = Integer.parseInt(args[0]);

	

		ServerSocketChannel ssc = setup();
		for (;;)
			serve(ssc);

	}

}