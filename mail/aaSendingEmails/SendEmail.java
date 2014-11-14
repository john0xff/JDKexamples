package aaSendingEmails;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail
{
	public static void main(String[] args)
	{
		// Recipient's email ID needs to be mentioned.
		String to = "spammer123@interia.pl";

		// Sender's email ID needs to be mentioned
		String from = "javamailap.@gmail.com";
		// username usually is the same as emale but without @gmail.com etc
		final String username = "javamailap.@gmail.com";// change accordingly
		final String password = "javamailap1";// change accordingly

		// Assuming you are sending email through relay.jangosmtp.net
		String host = "smtp.gmail.com";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

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
				message.setSubject("test " + i);
				message.setText(i + " Hello, this is sample for to check send " + "email using JavaMailAPI xxx ");
				
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