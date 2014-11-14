package samples.kBouncedMessages;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 * TODO setup properties and try to run ;)
 * 
 * 
 * 
 */
public class SendEmail
{
	public static void main(String[] args) throws Exception
	{
		String smtpServer = "smtp.gmail.com";
		int port = 587;
		final String userid = "youraddress";// change accordingly
		final String password = "*****";// change accordingly
		String contentType = "text/html";
		String subject = "test: bounce an email to a different address " + "from the sender";
		String from = "youraddress@gmail.com";
		String to = "bouncer@fauxmail.com";// some invalid address
		String bounceAddr = "toaddress@gmail.com";// change accordingly
		String body = "Test: get message to bounce to a separate email address";

		Properties props = new Properties();

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.port", "587");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.from", bounceAddr);

		Session mailSession = Session.getInstance(props, new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(userid, password);
			}
		});

		MimeMessage message = new MimeMessage(mailSession);
		message.addFrom(InternetAddress.parse(from));
		message.setRecipients(Message.RecipientType.TO, to);
		message.setSubject(subject);
		message.setContent(body, contentType);

		Transport transport = mailSession.getTransport();
		try
		{
			System.out.println("Sending ....");
			transport.connect(smtpServer, port, userid, password);
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			System.out.println("Sending done ...");
		}
		catch (Exception e)
		{
			System.err.println("Error Sending: ");
			e.printStackTrace();

		}
		transport.close();
	}// end function main()
}