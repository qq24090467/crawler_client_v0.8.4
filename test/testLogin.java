import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Response;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.sun.xml.internal.stream.Entity;

import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;

public class testLogin {

	private static String userName = "darkslayer27@126.com";
	private static String password = "xiaoda88327guan~";

	private static String redirectURL = "http://www.renren.com/home";// 没有用
																		// 使用getFirstHeader()从服务器取的

	// Don't change the following URL
	private static String renRenLoginURL = "http://www.renren.com/ajaxLogin/login?1=1&uniqueTimestamp=20141031547270";

	// The HttpClient is used in one session
	private HttpResponse response;// 用途
	private DefaultHttpClient httpclient = new DefaultHttpClient();// 一直使用的客户端

	private String login() {
		HttpPost httpost = new HttpPost(renRenLoginURL);
		// All the parameters post to the web site
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("origURL", redirectURL));
		nvps.add(new BasicNameValuePair("domain", "renren.com"));
		nvps.add(new BasicNameValuePair("isplogin", "true"));
		nvps.add(new BasicNameValuePair("formName", ""));
		nvps.add(new BasicNameValuePair("method", ""));
		nvps.add(new BasicNameValuePair("submit", "登录"));

		nvps.add(new BasicNameValuePair("email", userName));
		nvps.add(new BasicNameValuePair("password", password));
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			response = httpclient.execute(httpost);// 执行登录请求 返回信息
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			httpost.abort();// 不废除会报错
		}
		Header[] hs = response.getHeaders("Set-Cookie");
		String cookies = "";
		for (Header header : hs) {
//			System.out.println(header.getName() + "\t" + header.getValue());
			cookies += header.getValue() + ";";
		}
		try {
//			System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		
		
		return cookies;
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {
		testLogin t = new testLogin();

		String cookies = t.login();
		if (cookies != null)
			System.out.println("cookies:\t" + cookies);
		else {
			System.out.println("no.");
		}

		HttpPost hp = new HttpPost("http://www.renren.com/223950588");
		DefaultHttpClient hc= new DefaultHttpClient();
		HttpResponse response=hc.execute(hp);
		String content=EntityUtils.toString(response.getEntity());
		System.out.println(content);

	}

}
