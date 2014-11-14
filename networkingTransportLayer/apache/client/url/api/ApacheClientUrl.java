package apache.client.url.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class ApacheClientUrl
{
	private String cookies;

	private String url = "http://phoenixjcam.no-ip.biz/index.html";
	private HttpClient client;
	private final String USER_AGENT = "Mozilla/5.0";

	public ApacheClientUrl()
	{
		client = HttpClientBuilder.create().build();

		HttpGet request = new HttpGet(url);

		try
		{
			client.execute(request);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static void main(String[] args)
	{
		new ApacheClientUrl();

		// make sure cookies is turn on
		// CookieHandler.setDefault(new CookieManager());

		// ApacheClientUrl http = new ApacheClientUrl();

		// String page = http.GetPageContent(url);

		// List<NameValuePair> postParams = http.getFormParams(page, "username", "password");
		//
		// http.sendPost(url, postParams);
		//
		// String result = http.GetPageContent(gmail);
		// System.out.println(result);
		//
		// System.out.println("Done");
	}

	private String GetPageContent(String url) throws Exception
	{

		HttpGet request = new HttpGet(url);

		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language", "en-US,en;q=0.5");

		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null)
		{
			result.append(line);
		}

		// set cookies
		// setCookies(response.getFirstHeader("Set-Cookie") == null ? "" :
		// response.getFirstHeader("Set-Cookie").toString());

		return result.toString();

	}

}
