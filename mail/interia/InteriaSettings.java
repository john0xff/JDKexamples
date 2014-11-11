
package interia;


import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//import javax.mail.Message;
/**
 * <pre>
 * --------------------------------------
 * --------------------------------------
 * interia mail server - settings 
 * --------------------------------------
 * --------------------------------------
 * server incoming - IMAP/POP3	poczta.interia.pl
 * server out coming - SMTP	poczta.interia.pl
 * 
 * The best:
 * IMAP: 993 (SSL)
 * SMTP: 465 (SSL) 
 * 
 * Alternative: 
 * IMAP: 143 (TLS) 
 * POP3: 110 
 * SPOP3: 995 
 * SMTP: 587
 * 
 * 
 * </pre>
 * 
 * @author BartBien
 */
public class InteriaSettings
{

	public InteriaSettings()
	{

	}

	public static void main(String[] args)
	{
		// Recipient's email ID needs to be mentioned.
		String to = ""; // whatever

		// Sender's email ID needs to be mentioned
		String from = ""; // change accordingly
		final String username = "";// change accordingly
		final String password = "";// change accordingly

		// Assuming you are sending email through relay.jangosmtp.net
		String host = "poczta.interia.pl";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true"); // tsl
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587"); // interia standard SMTP

		// Get the Session object.
		Session session = Session.getInstance(props, new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(username, password);
			}
		});

		try
		{
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

			// Set Subject: header field
			message.setSubject("Testing Subject");

			// Now set the actual message
			message.setText("Hello, this is sample for to check send " + "email using JavaMailAPI ");

			// Send message
			Transport.send(message);

			System.out.println("Sent message successfully....");

		}
		catch (MessagingException e)
		{
			throw new RuntimeException(e);
		}
	}
}
