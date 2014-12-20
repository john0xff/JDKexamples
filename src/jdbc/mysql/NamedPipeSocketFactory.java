//package jdbc.mysql;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.RandomAccessFile;
//import java.net.Socket;
//import java.net.SocketException;
//import java.sql.SQLException;
//import java.util.Properties;
//
//public class NamedPipeSocketFactory
//  implements SocketFactory, SocketMetadata
//{
//  public static final String NAMED_PIPE_PROP_NAME = "namedPipePath";
//  private Socket namedPipeSocket;
//
//  public Socket afterHandshake()
//    throws SocketException, IOException
//  {
//    return this.namedPipeSocket;
//  }
//
//  public Socket beforeHandshake()
//    throws SocketException, IOException
//  {
//    return this.namedPipeSocket;
//  }
//
//  public Socket connect(String host, int portNumber, Properties props)
//    throws SocketException, IOException
//  {
//    String namedPipePath = props.getProperty("namedPipePath");
//
//    if (namedPipePath == null)
//      namedPipePath = "\\\\.\\pipe\\MySQL";
//    else if (namedPipePath.length() == 0) {
//      throw new SocketException(Messages.getString("NamedPipeSocketFactory.2") + "namedPipePath" + Messages.getString("NamedPipeSocketFactory.3"));
//    }
//
//    this.namedPipeSocket = new NamedPipeSocket(namedPipePath);
//
//    return this.namedPipeSocket;
//  }
//
//  public boolean isLocallyConnected(ConnectionImpl conn) throws SQLException
//  {
//    return true;
//  }
//
//  class RandomAccessFileOutputStream extends OutputStream
//  {
//    RandomAccessFile raFile;
//
//    RandomAccessFileOutputStream(RandomAccessFile file)
//    {
//      this.raFile = file;
//    }
//
//    public void close()
//      throws IOException
//    {
//      this.raFile.close();
//    }
//
//    public void write(byte[] b)
//      throws IOException
//    {
//      this.raFile.write(b);
//    }
//
//    public void write(byte[] b, int off, int len)
//      throws IOException
//    {
//      this.raFile.write(b, off, len);
//    }
//
//    public void write(int b)
//      throws IOException
//    {
//    }
//  }
//
//  class RandomAccessFileInputStream extends InputStream
//  {
//    RandomAccessFile raFile;
//
//    RandomAccessFileInputStream(RandomAccessFile file)
//    {
//      this.raFile = file;
//    }
//
//    public int available()
//      throws IOException
//    {
//      return -1;
//    }
//
//    public void close()
//      throws IOException
//    {
//      this.raFile.close();
//    }
//
//    public int read()
//      throws IOException
//    {
//      return this.raFile.read();
//    }
//
//    public int read(byte[] b)
//      throws IOException
//    {
//      return this.raFile.read(b);
//    }
//
//    public int read(byte[] b, int off, int len)
//      throws IOException
//    {
//      return this.raFile.read(b, off, len);
//    }
//  }
//
//  class NamedPipeSocket extends Socket
//  {
//    private boolean isClosed = false;
//    private RandomAccessFile namedPipeFile;
//
//    NamedPipeSocket(String filePath)
//      throws IOException
//    {
//      if ((filePath == null) || (filePath.length() == 0)) {
//        throw new IOException(Messages.getString("NamedPipeSocketFactory.4"));
//      }
//
//      this.namedPipeFile = new RandomAccessFile(filePath, "rw");
//    }
//
//    public synchronized void close()
//      throws IOException
//    {
//      this.namedPipeFile.close();
//      this.isClosed = true;
//    }
//
//    public InputStream getInputStream()
//      throws IOException
//    {
//      return new NamedPipeSocketFactory.RandomAccessFileInputStream(NamedPipeSocketFactory.this, this.namedPipeFile);
//    }
//
//    public OutputStream getOutputStream()
//      throws IOException
//    {
//      return new NamedPipeSocketFactory.RandomAccessFileOutputStream(NamedPipeSocketFactory.this, this.namedPipeFile);
//    }
//
//    public boolean isClosed()
//    {
//      return this.isClosed;
//    }
//  }
//}