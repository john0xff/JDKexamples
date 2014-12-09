package streams.TODO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 1. Scanner Writer 2. Scanner Reader 3. Streams on low level - reader and writer / byte/chars 4. FileInputStream 5.
 * FileOutputStream
 * 
 * 
 * 
 * 
 * @author BartBien
 *
 */
public class TODO
{
	// sun streams :)
	// StreamEncoder;
	
	ObjectOutputStream objectOutputStream;
	ObjectInputStream objectInputStream;

	FileOutputStream fileOutputStream = null;
	FileInputStream fileInputStream;

	FileWriter fileWriter;
	FileReader fileReader;

	BufferedReader in;
	BufferedWriter out;

	InputStreamReader inputStreamReader;
	OutputStreamWriter outputStreamWriter;

	DataOutputStream dataOutputStream;
	DataInputStream dataInputStream;

	DataInput dataInput;
	DataOutput dataOutput;

	Reader reader;
	Writer writer;

	StringReader stringReader;
	StringWriter stringWriter;
	
	// BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(""), "UTF8"));
}
