package common.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import common.bean.HtmlInfo;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.util.EncoderUtil;
import common.util.MD5Util;
import common.util.StringUtil;
import common.util.UserAgent;

/**
 * 普通http请求处理
 * 
 * @author grs
 * @since 2014年1月
 */
public class SimpleHttpProcess implements HttpProcess {
	private static final String[] charsets = new String[] { "utf-8", "gbk", "gb2312", "big5" };
	protected int readTime = 180000;
	protected int connectNum = 3;
	protected int requestTime = 120000;
	protected static final String userAgent = UserAgent.getUserAgent();
	/**
	 * 创建连接
	 * 
	 * @param module
	 * @return
	 */
	protected static final ConcurrentHashMap<String, HttpClient> clientMap = new ConcurrentHashMap<String, HttpClient>();

	int count = 0;

	@Override
	public HttpClient httpClient(HtmlInfo html) {
		String key = html.getType();
		if (clientMap.containsKey(key)) {
			if (count++ > 100) {
				HttpParams params = httpParams(html.getAgent());
				HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(), params);

				clientMap.putIfAbsent(key, client);
				count = 0;
			}

			return clientMap.get(key);
		} else {
			HttpParams params = httpParams(html.getAgent());
			HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(), params);

			clientMap.putIfAbsent(key, client);
			return client;
		}
	}

	@Override
	public void getContent(HtmlInfo html, UserAttr user) {
		byte[] fromURL = null;
		try {
			fromURL = user == null ? simpleGet(html) : simpleGet(html, user);
			if (fromURL == null) {
				Systemconfig.sysLog.log("没有抓取到内容，建议暂停采集！请检查网络链接或URL：" + html.getOrignUrl() + "是否正确。");
				html.setContent(null);
				return;
			}
			if (html.getFileType().equals(".htm")) {
				htm(html, fromURL);
			} else {
				pdf(html, fromURL);
			}

		} finally {
			fromURL = null;
		}
	}

	private void htm(HtmlInfo html, byte[] fromURL) {
		String con = null;
		try {
			con = new String(fromURL, html.getEncode().replace(";", ""));//神奇的bug
			if (!html.getFixEncode()) {// 需要更换encode，检测页面内容的charset
				String charset = StringUtil.regMatcher(con, "charset=\"?", "[\"/]");
				if (charset != null && charset.length() < 11 && charset.length() > 2) {
					charset = charset.trim().toLowerCase();
					if (!html.getEncode().equals(charset)) {
						for (String s : charsets) {
							if (charset.contains(s)) {
								html.setEncode(s);
								break;
							}
						}
						con = null;
						con = new String(fromURL, html.getEncode());
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			Systemconfig.sysLog.log("请检查并输入正确字符集！", e);
		}
		html.setContent(con);
		if (Systemconfig.createFile) {
			String tmp = Systemconfig.filePath + File.separator + html.getType() + File.separator
					+ MD5Util.MD5(html.getOrignUrl()) + html.getFileType();
			if (html.getAddHead()) {
				StringUtil.writeFile(tmp, html.getOrignUrl() + "\r\n" + html.getContent(), html.getEncode());
			} else {
				StringUtil.writeFile(tmp, html.getContent(), html.getEncode());
			}
		}
		con = null;
	}

	private void pdf(HtmlInfo html, byte[] fromURL) {
		FileOutputStream fos = null;
		try {
			// fos = new FileOutputStream(Systemconfig.filePath + File.separator
			// + html.getType() + File.separator
			// + MD5Util.MD5(html.getOrignUrl()) + html.getFileType());
			// fos.write(fromURL);
			String folderName = Systemconfig.filePath + File.separator + html.getType();
			if (!new File(folderName).exists())
				new File(folderName).mkdirs();
			String fileName = folderName + File.separator + MD5Util.MD5(html.getOrignUrl()) + html.getFileType();
			fos = new FileOutputStream(fileName);
			fos.write(fromURL);
			Systemconfig.sysLog.log("pdf保存至： " + fileName);
		} catch (IOException e) {
			Systemconfig.sysLog.log("文件无法下载。", e);
		} finally {
			if (fos != null)
				try {
					fos.close();

				} catch (IOException e) {
					fos = null;
				}
		}

	}

	protected byte[] simpleGet(HtmlInfo html, UserAttr user) {
		return simpleGet(html);
	}

	@Override
	public void getContent(HtmlInfo html) {
		getContent(html, null);
	}

	/**
	 * 普通的get请求
	 * 
	 * @param url
	 * @return
	 */
	protected byte[] simpleGet(HtmlInfo html) {
		HttpClient hc = httpClient(html);
		HttpGet get = new HttpGet(EncoderUtil.encodeKeyWords(html.getOrignUrl(), "utf-8"));
		if (html.getCookie() != null) {
			get.setHeader("Cookie", html.getCookie());
		}
		if (html.getReferUrl() != null) {
			get.setHeader("Referer", html.getReferUrl());
		}
		if (html.getUa() != null) {
			get.setHeader("User-Agent", html.getUa());
		}

		InputStream in = null;
		HttpEntity responseEntity = null;
		try {
			HttpResponse response = hc.execute(get);
			// if(response.getStatusLine().getStatusCode() == 200) {
			responseEntity = response.getEntity();
			if (responseEntity != null) {
				in = response.getEntity().getContent();
				Header h = response.getFirstHeader("Content-Type");
				if (h != null && h.getValue().indexOf("charset") > -1) {
					html.setEncode(h.getValue().substring(h.getValue().indexOf("charset=") + 8).replace(";", ""));
					html.setFixEncode(true);
				}
				return EntityUtils.toByteArray(responseEntity);
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(responseEntity);
			} catch (IOException e) {
				// e.printStackTrace();
			}
			IOUtils.closeQuietly(in);
			get.abort();
		}
		return null;
	}

	protected HttpParams httpParams(boolean agent) {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
		params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		params.setParameter(ClientPNames.MAX_REDIRECTS, connectNum);
		params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, requestTime);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, readTime);
		if (agent) {
			HttpHost proxy = new HttpHost(Systemconfig.agentIp, Systemconfig.agentPort);
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		return params;
	}

	/**
	 * 创建安全连接协议httpclient
	 * 
	 * @param client
	 */
	public HttpClient tlsClient(HttpClient client) {
		try {
			// TLS单项认证，SSL双向认证
			SSLContext ctx = SSLContext.getInstance(SSLSocketFactory.TLS);
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			X509HostnameVerifier verifier = new X509HostnameVerifier() {
				@Override
				public void verify(String string, SSLSocket ssls) throws IOException {
				}

				@Override
				public void verify(String string, X509Certificate xc) throws SSLException {
				}

				@Override
				public void verify(String string, String[] strings, String[] strings1) throws SSLException {
				}

				@Override
				public boolean verify(String string, SSLSession ssls) {
					return true;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, verifier);
			ClientConnectionManager ccm = client.getConnectionManager();
			ccm.getSchemeRegistry().register(new Scheme("https", 443, ssf));
			return new DefaultHttpClient(ccm, client.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	protected byte[] data(HttpEntity entity) {
		byte[] buffer = new byte[2048];
		byte[] byteArray = null;
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		int count = -1;
		InputStream responseBodyAsStream = null;
		try {
			responseBodyAsStream = entity.getContent();
			Header contentEncodingHeader = entity.getContentEncoding();
			if (contentEncodingHeader != null) {
				String contentEncoding = contentEncodingHeader.getValue();
				if (contentEncoding.toLowerCase(Locale.US).indexOf("gzip") != -1) {
					responseBodyAsStream = new GZIPInputStream(responseBodyAsStream);
				}
			}
			while ((count = responseBodyAsStream.read(buffer, 0, buffer.length)) > -1) {
				baos.write(buffer, 0, count);
			}
			byteArray = baos.toByteArray();
			return byteArray;
		} catch (IOException e) {
		} finally {
			buffer = null;
			try {
				baos.close();
				responseBodyAsStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void monitorLogin(UserAttr user) {
	}

	public String getJsonContent(String url, String charSet) {
		String code = StringUtil.regMatcher(url, "/p-", "-s-0-t-3-p-0");
		String refer = "http://item.jd.com/" + code + ".html";
		String content1 = "";
		String content2 = "";
		try {
			content1 = getJsonContentGb2312(url, refer);
			content2 = getJsonContentUtf8(url, refer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String content = "";
		if (charSet.equals("gbk") || charSet.equals("gb2312"))
			content = content1;
		else if (charSet.equals("utf-8"))
			content = content2;
		else {
			content = content2;
		}
		// if (StringUtil.regMatcher(content1, "\\(", "\\)") != null){
		// Systemconfig.sysLog.log("encoding: gb2312");
		// return content1;
		// }
		// if (StringUtil.regMatcher(content2, "\\(", "\\)") != null){
		// Systemconfig.sysLog.log("encoding: utf8");
		// return content2;
		// }
		if (content == null || !content.contains("{"))
			return null;
		content = content.startsWith("{") ? content : content.substring(content.indexOf("{"),
				content.lastIndexOf("}") + 1);
		return content;
		// return content1.length() > content2.length() ? content1 : content2;
	}

	public String getJsonContentUtf8(String url, String... args) {
		HtmlInfo html = new HtmlInfo();
		html.setType("DATA");
		html.setOrignUrl(url);
		html.setEncode("utf-8");
		html.setReferUrl(args[0]);
		getContent(html);

		return html.getContent();
	}

	public String getJsonContentGb2312(String url, String... args) {
		HtmlInfo html = new HtmlInfo();
		html.setType("DATA");
		html.setOrignUrl(url);
		html.setEncode("gb2312");
		html.setReferUrl(args[0]);
		getContent(html);

		return html.getContent();
	}

	@Override
	public String getJsonContent(String ownerInitUrl) {
		// TODO Auto-generated method stub
		return null;
	}

}
