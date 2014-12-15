package streams.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;

/**
 * 
 * https://docs.oracle.com/javase/7/docs/api/javax/xml/bind/Marshaller.html
 * 
 * <pre>
 * http://www.programcreek.com/java-api-examples/index.php?api=javax.xml.transform.stream.StreamSource
 * 
 * 
 * Entire API for xml processing in javax.xml packages
 * 
 * @author b.bien
 *
 */
public class XmlProcessing
{
	javax.xml.transform.stream.StreamSource streamSource;

	// JAXBContext

	public XmlProcessing()
	{
		File file = new File("somePath to file");

		try
		{
			FileInputStream fis = new FileInputStream(file);

		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

// /**
// * Unmarshalls the XML encoded message in the {@link TextMessage} to anObject
// */
// protected Object unmarshall(Session session,TextMessage textMessage) throws JMSException {
// try {
// String text=textMessage.getText();
// Source source=new StreamSource(new StringReader(text));
// return marshaller.unmarshal(source);
// }
// catch ( Exception e) {
// throw new JMSException(e.getMessage());
// }
// }
