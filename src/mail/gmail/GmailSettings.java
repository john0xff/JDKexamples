
package mail.gmail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * <pre>
 * SSL
 * TSL
 * SMTP
 * POP3
 * IMAP
 * 
 * 
 * 
 * 
 * @author BartBien
 *
 */
public class GmailSettings
{

	// spammer123@interia.pl
	// password: asdfg12345
	public static void main(String[] args)
	{

		// Recipient's email ID needs to be mentioned.
		String to = ""; 

		// Sender's email ID needs to be mentioned
		String from = "....@gmail.com"; // gmail settings
		// username usually is the same as emale but without @gmail.com etc
		final String username = "...@gmail.com";// change accordingly
		final String password = "...";// change accordingly

		// Assuming you are sending email through relay.jangosmtp.net
		String host = "smtp.gmail.com";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587"); 

		// Gmail SMTP port (TLS): 587
		// Gmail SMTP port (SSL): 465
		// Gmail SMTP TLS/SSL required: yes

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

			//

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

			// Set Subject: header field
			message.setSubject("Testing Subject");

			// Now set the actual message

			int i = 0;

			for (; i < 2; i++)
			{
				message.setSubject("aaaaa" + i);
				message.setText(i + " aaaaaaaaaaaaaaaaaaaa");

				Transport.send(message);

				System.out.println(i);
			}

			System.out.println("Sent message successfully...." + i);

		}
		catch (MessagingException e)
		{
			throw new RuntimeException(e);
		}
	}
}