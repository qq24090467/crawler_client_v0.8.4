package common.http.sub;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import common.bean.HtmlInfo;
import common.http.NeedCookieHttpProcess;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.util.JsonUtil;
import common.util.StringUtil;
import common.util.TimeUtil;

public class TiebaHttpProcess extends NeedCookieHttpProcess {

	@Override
	public void getContent(HtmlInfo html, UserAttr user) {
		if(user == null) {
			Systemconfig.sysLog.log("没有可用采集登陆用户！");
			return;
		}
		if(user.getCookie() != null && !user.getCookie().equals("")) 
			cookie = user.getCookie();
		else
			login(user);
		
		super.getContent(html, user);
	}
	@Override
	public byte[] simpleGet(HtmlInfo html, UserAttr user) {
		HttpClient hc = httpClient(html);
		HttpGet get = new HttpGet(html.getOrignUrl());
		get.addHeader("User-Agent", user==null?userAgent:user.getUserAgent());
		
		get.addHeader("Cookie", cookie);
		InputStream in = null;
		HttpEntity responseEntity = null;
		try {
			HttpResponse response = hc.execute(get);
			responseEntity = response.getEntity();
			if (responseEntity != null) {
				if(responseEntity.isChunked()) {
					return data(responseEntity);
				} else
					return EntityUtils.toByteArray(responseEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(responseEntity);
			} catch (IOException e) {
				e.printStackTrace();
			}
			IOUtils.closeQuietly(in);
			get.abort();
		}
		return null;
	}
	
	@Override
	public boolean login(UserAttr user) {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://www.baidu.com");
		HttpResponse re = null;
		try {
			re = client.execute(get);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			get.abort();
		}
		Header[] headers = re.getHeaders("Set-Cookie");
		for (Header h : headers) {
			if (!h.getValue().startsWith("BDSVRTM"))
				cookie += h.getValue().split(";")[0] + "; ";
		}

		client = tlsClient(client);
		get = new HttpGet("https://passport.baidu.com/v2/api/?getapi&tpl=mn&apiver=v3&class=login");
		get.addHeader("Cookie", cookie);
		get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:26.0) Gecko/20100101 Firefox/26.0");
		get.addHeader("Referer", "http://www.baidu.com");
		try {
			re = client.execute(get);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			get.abort();
		}

		headers = re.getHeaders("Set-Cookie");
		for (Header h : headers) {
			cookie += h.getValue().split(";")[0] + "; ";
		}

		String content = null;
		try {
			content = new String(EntityUtils.toString(re.getEntity()));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String token = JsonUtil.getJsonObjectByKey(content, "data").getString(
				"token");

		get = new HttpGet("https://passport.baidu.com/v2/api/?loginhistory&token="
						+ token + "&tpl=mn&apiver=v3");
		get.addHeader("Cookie", cookie);
		get.addHeader("User-Agent","Mozilla/5.0 (Windows NT 5.1; rv:26.0) Gecko/20100101 Firefox/26.0");
		get.addHeader("Referer", "http://www.baidu.com");
		try {
			re = client.execute(get);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			get.abort();
		}

		headers = re.getHeaders("Set-Cookie");
		for (Header h : headers) {
			if (h.getValue().startsWith("UBI"))
				cookie += h.getValue().split(";")[0];
		}
		get = new HttpGet("https://passport.baidu.com/v2/api/?logincheck&token=" + token
						+ "&tpl=mn&apiver=v3&username="+user.getName()+"&isphone=false");
		get.addHeader("Cookie", cookie);
		get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:26.0) Gecko/20100101 Firefox/26.0");
		get.addHeader("Referer", "http://www.baidu.com");
		try {
			re = client.execute(get);
			content = new String(EntityUtils.toString(re.getEntity()));
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			get.abort();
		}
		
		String next = JsonUtil.getJsonObjectByKey(content, "data").getString(
				"codeString");
		boolean codestring = false;
		String vcode = "";
		if (!next.equals("")) {
			codestring = true;
			get = new HttpGet("https://passport.baidu.com/cgi-bin/genimage?" + next);
			get.addHeader("Cookie", cookie);
			get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:26.0) Gecko/20100101 Firefox/26.0");
			get.addHeader("Referer", "http://www.baidu.com");
			try {
				re = client.execute(get);
				StringUtil.writeFile("baidu.jpg", EntityUtils.toByteArray(re.getEntity()));
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				get.abort();
			}
//				Scanner sc = new Scanner(System.in);
//				vcode = sc.nextLine();
			
			while(vcode.equals("")){
				File file = new File("baidu.txt");
				try {
					if(file.exists()){
						vcode = StringUtil.getContent("baidu.txt").replace("\n", "");
						vcode = vcode.replace(" ", "").replace("\r", "").replace("\t", "");
					}else{
						TimeUtil.rest(1);
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
					TimeUtil.rest(60);
					continue;
				}
				file.delete();
				file = new File("baidu.jpg");
				file.delete();
			}
			
		}

		HttpPost post = new HttpPost("https://passport.baidu.com/v2/api/?login");
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("staticpage",
				"http://www.baidu.com/cache/user/html/v3Jump.html"));
		list.add(new BasicNameValuePair("charset", "UTF-8"));
		list.add(new BasicNameValuePair("token", token.trim()));
		list.add(new BasicNameValuePair("tpl", "mn"));
		list.add(new BasicNameValuePair("apiver", "v3"));
		list.add(new BasicNameValuePair("tt", System.currentTimeMillis() + ""));
		if (codestring)
			list.add(new BasicNameValuePair("codestring", next));
		else
			list.add(new BasicNameValuePair("codestring", ""));

		list.add(new BasicNameValuePair("safeflg", "0"));
		list.add(new BasicNameValuePair("u", "http://www.baidu.com/"));
		list.add(new BasicNameValuePair("isPhone", ""));
		list.add(new BasicNameValuePair("quick_user", "0"));
		list.add(new BasicNameValuePair("loginmerge", "true"));
		list.add(new BasicNameValuePair("logintype", "dialogLogin"));
		list.add(new BasicNameValuePair("splogin", "rate"));
		list.add(new BasicNameValuePair("username", user.getName()));
		list.add(new BasicNameValuePair("password", user.getPass()));
		list.add(new BasicNameValuePair("verifycode", vcode));

		list.add(new BasicNameValuePair("mem_pass", "on"));
		list.add(new BasicNameValuePair("ppui_logintime", "14589"));
		list.add(new BasicNameValuePair("callback", "parent.bd__pcbs__efkrdn"));

		try {
			post.setEntity(new UrlEncodedFormEntity(list));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)");
		post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		post.setHeader("Accept-Encoding", "gzip, deflate");
		post.setHeader("Referer", "http://www.baidu.com");
		post.setHeader("Cookie", cookie);
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			re = client.execute(post);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.abort();
		}
		Header[] hs = re.getHeaders("Set-Cookie");

		cookie = "BAIDUID" + StringUtil.regMatcher(cookie, "BAIDUID", ";")
				+ "; H_PS_PSSID=;";
		for (Header h : hs) {
			if (h.getValue().startsWith("BDUSS"))
				cookie += h.getValue().split(";")[0] + ";";
		}
		post.abort();

		get = new HttpGet("http://tieba.baidu.com");
		get.addHeader("Referer",
				"http://www.baidu.com/ulink?url=http%3A%2F%2Ftieba.baidu.com&");
		get.setHeader("Cookie", cookie);

		try {
			re = client.execute(get);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			get.abort();
		}
		hs = re.getHeaders("Set-Cookie");
		for (Header h : hs) {
			if (!cookie.contains("TIEBAUID"))
				cookie += h.getValue().split(";")[0] + ";";
		}
		cookie += "bdshare_firstime=" + (System.currentTimeMillis() - 10000) + ";";
		cookie += "fuwu_center_bubble=1;";
		cookie += "PMS_JT=%28%7B%22s%22%3A" + System.currentTimeMillis() + "%2C%22r%22%3A%22http%3A//tieba.baidu.com/%22%7D%29;";
		user.setCookie(cookie);
		return true;
	}
	
	@Override
	public boolean verify(UserAttr user) {
		HtmlInfo html = new HtmlInfo();
		html.setOrignUrl("http://tieba.baidu.com");
		html.setSite("baidu");
		html.setEncode("gb2312");
		String con = new String(simpleGet(html));
		if(con.contains("user_name\":\""+user.getName()+"\"")) {
			return true;
		}
		return false;
	}
	
	private static final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
	@Override
	public void monitorLogin(UserAttr user) {
		ses.scheduleAtFixedRate(new VerifyCookie(user), 2, 2, TimeUnit.HOURS);
	}
	
	class VerifyCookie implements Runnable {
		private UserAttr user;
		public VerifyCookie(UserAttr user) {
			this.user = user;
		}

		@Override
		public void run() {
			if(!verify(user))
				login(user);
		}
	}

}
