package jQuotaManagement;

import java.util.Properties;

import javax.mail.Quota;
import javax.mail.Session;
import javax.mail.Store;

import com.sun.mail.imap.IMAPStore;

/**
 * 
 * TODO setup properties and try to run ;) and fill connect with rights
 * 
 * 
 */
public class QuotaExample
{
	public static void main(String[] args)
	{
		try
		{
			Properties properties = new Properties();
			properties.put("mail.store.protocol", "imaps");
			properties.put("mail.imaps.port", "993");
			properties.put("mail.imaps.starttls.enable", "true");
			Session emailSession = Session.getDefaultInstance(properties);
			// emailSession.setDebug(true);

			// create the IMAP3 store object and connect with the pop server
			Store store = emailSession.getStore("imaps");

			// change the user and password accordingly
			store.connect("imap.gmail.com", "abc@gmail.com", "*****");
			IMAPStore imapStore = (IMAPStore) store;
			System.out.println("imapStore ---" + imapStore);

			// get quota
			Quota[] quotas = imapStore.getQuota("INBOX");
			// Iterate through the Quotas
			for (Quota quota : quotas)
			{
				System.out.println(String.format("quotaRoot:'%s'", quota.quotaRoot));
				// Iterate through the Quota Resource
				for (Quota.Resource resource : quota.resources)
				{
					System.out.println(String.format("name:'%s', limit:'%s', usage:'%s'", resource.name, resource.limit, resource.usage));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}