package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TwitterLogin {
	private final String USER_AGENT = "Mozilla/5.0";
	private String cookies;
	private String twitter_sess;
	private String guest_id;
	private String authenticity_token;

	private String auth_token;
	private String twid;

	private HttpClient client = HttpClientBuilder.create().build();

	public static void main(String[] args) {
		String urlLogin = "https://twitter.com/login";
		String urlsess = "https://twitter.com/sessions";
		String urlmain = "https://twitter.com/";

		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		TwitterLogin http = new TwitterLogin();

		/*
		 * try { http.sendPostfirst(urlLogin); } catch (Exception e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }
		 */

		String result = "";
		try {
			String page = http.GetPageContent(urlLogin);
			
			List<NameValuePair> postParams = http.getFormParams(page, "user",	"pass");
			http.sendPost(urlsess, postParams);
			result = http.GetPageContent(urlmain);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(result);

		System.out.println("Done");
	}

	private void sendPostfirst(String url) throws Exception {

		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("Host", "twitter.com");
		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language",
				"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,bg;q=0.2");
		post.setHeader("Cookie", getCookies());
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Referer", "https://twitter.com/");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		// post.setEntity(new UrlEncodedFormEntity(postParams));

		HttpResponse response = client.execute(post);

		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Request : " + post.toString());
		// System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);
		System.out.println("Response : " + response.toString());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(
				"d:/demo.txt")));
		// write contents of StringBuffer to a file
		bwr.write(result.toString());
		// flush the stream
		bwr.flush();
		// close the stream
		bwr.close();
		// System.out.println(result.toString());

		System.out.println("Cookies : "
				+ collectCookiesresponse(response.getHeaders("set-cookie")));

	}

	private void sendPost(String url, List<NameValuePair> postParams)
			throws Exception {

		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("Host", "twitter.com");
		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language",
				"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,bg;q=0.2");
		post.setHeader("Cookie", getCookies());
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Referer",
				"https://twitter.com/download?logged_out=1&lang=ru");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));

		HttpResponse response = client.execute(post);

		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		//System.out.println(result.toString());

	}

	private String GetPageContent(String url) throws Exception {

		HttpGet request = new HttpGet(url);

		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language",
				"ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,bg;q=0.2");

		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		// set cookies
		setCookies(response.getFirstHeader("set-cookie") == null ? ""
				: collectCookiesresponse(response.getHeaders("set-cookie")));

		return result.toString();

	}

	private String collectCookiesresponse(Header[] headers) {
		StringBuilder result = new StringBuilder();
		for (Header header : headers) {
			if (result.length() == 0) {
				result.append(header.toString());
			} else {
				result.append(";" + header.getValue());
			}
		}
		return result.toString();
	}

	public List<NameValuePair> getFormParams(String html, String username,
			String password) throws UnsupportedEncodingException {

		System.out.println("Extracting form's data...");
		Document doc = Jsoup.parse(html);

		// Login form id
		Element loginform = doc.getElementsByClass("LoginForm js-front-signin").first();// getElementById("LoginForm.js-front-signin");
		Elements inputElements = loginform.getElementsByTag("input");

		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (key.equals("session[username_or_email]"))
				value = username;
			else if (key.equals("session[password]"))
				value = password;
			else if (key.equals("remember_me"))
				value = "0";

			paramList.add(new BasicNameValuePair(key, value));

		}
		/*paramList.add(new BasicNameValuePair("session[username_or_email]",
				URLEncoder.encode(username, "UTF-8")));
		paramList.add(new BasicNameValuePair("session[password]", URLEncoder
				.encode(password, "UTF-8")));

		paramList.add(new BasicNameValuePair("remember_me", "0")); */

		/*
		 * return_to_ssl:true
		 * 
		 * scribe_log: redirect_after_login:/
		 * authenticity_token:aaac16aa87f3765e27cc55b004b3dde4acc38e6c
		 */

		return paramList;
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

}
