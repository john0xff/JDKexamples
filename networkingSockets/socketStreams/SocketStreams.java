package socketStreams;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SocketStreams
{
	// -------------------------------------------------------------------------
	// class SocketOutputStream extends FileOutputStream
	/**
	 * Writes to the socket.
	 * 
	 * @param fd
	 *            the FileDescriptor
	 * @param b
	 *            the data to be written
	 * @param off
	 *            the start offset in the data
	 * @param len
	 *            the number of bytes that are written
	 * @exception IOException If an I/O error has occurred.
	 */
	private native void socketWrite0(FileDescriptor fd, byte[] b, int off, int len) throws IOException;

	// -------------------------------------------------------------------------

	// FileOutputStream couple of native method
	private native void open(String name, boolean append) throws FileNotFoundException;

	private native void write(int b, boolean append) throws IOException;

	private native void writeBytes(byte b[], int off, int len, boolean append) throws IOException;
	// -------------------------------------------------------------------------
}
