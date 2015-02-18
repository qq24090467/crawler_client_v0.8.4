package common.http.sub;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import common.bean.HtmlInfo;
import common.http.NeedCookieHttpProcess;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.util.EncoderUtil;
import common.util.StringUtil;

/**
 * 新浪微博
 * @author grs
 *
 */
public class SinaHttpProcess extends NeedCookieHttpProcess {
	
	private String redirectURL;
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	@Override
	public void getContent(HtmlInfo html, UserAttr ua) {
		if(ua != null)
			redirectURL = ua.getReferer();
		super.getContent(html, ua);
	}
	
	@Override
	protected byte[] simpleGet(HtmlInfo html, UserAttr user) {
		HttpClient hc = httpClient(html);
		HttpGet get = new HttpGet(html.getOrignUrl());
		get.addHeader("User-Agent",user==null?userAgent:user.getUserAgent());
		
		if(html.getType().startsWith("LOGIN"))
			get.setHeader("Content-Type", "application/x-www-form-urlencoded");
		if(redirectURL != null){
			get.setHeader("Referer", redirectURL);
		}
		
		if (cookie != null && !cookie.equals("")) {
			get.setHeader("Cookie", cookie);
		} else if(!html.getType().contains("LOGIN")) {
			if(user.getCookie() != null && !user.getCookie().equals("")) 
				cookie = user.getCookie();
			else if(!login(user)) {
				Systemconfig.sysLog.log(user.getName()+"登陆失败！没有采集到数据");
				return null;
			}
			get.setHeader("Cookie", cookie);
		}
		
		InputStream in = null;
		HttpEntity responseEntity = null;
		try {
			HttpResponse response = hc.execute(get);
			responseEntity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) 
				redirectURL = get.getURI().toString();
			if(user != null)
				user.setReferer(redirectURL);
			if (responseEntity != null) {
				Header h = response.getFirstHeader("Content-Type");
				if(h != null && h.getValue().indexOf("charset") > -1) {
					html.setEncode(h.getValue().substring(h.getValue().indexOf("charset=")+8));
					html.setFixEncode(true);
				}
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
	
	public HttpResponse cookie(HtmlInfo html, String agent) {
		HttpClient hc = httpClient(html);
		HttpGet get = new HttpGet(html.getOrignUrl());
		get.addHeader("Content-Type", "application/x-www-form-urlencoded");
		get.addHeader("User-Agent",agent==null?userAgent:agent);
		get.addHeader("Referer", html.getReferUrl());
		try {
			HttpResponse response = hc.execute(get);
			cookie = getCookie(response);
			return response;
		} catch (Exception e) {
			return null;
		} finally {
			get.abort();
		}
	}
	
	public String getRedirectURL() {
		return redirectURL;
	}
	
	public  String getCookie(HttpResponse response) {
		Header[] hs = response.getHeaders("Set-Cookie");
		StringBuilder sb = new StringBuilder();
		for(Header h : hs) {
			sb.append(h.getValue().split(";")[0] + "; ");
		}
		return sb.toString();
	}
	
	@Override
	public synchronized boolean login(UserAttr user) {
		if(user == null) {
			Systemconfig.sysLog.log("用户不存在，数据无法采集！");
			return false;
		}
		HtmlInfo html = new HtmlInfo();
		html.setEncode("utf-8");
		html.setType("LOGIN");
		html.setSite("sina");
		
		SinaUserLoginEntity loginEntity = getEntity(user);
		String postLoginUrl = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.2)";
		String pwdString = loginEntity.getServertime() + "\t"
				+ loginEntity.getNonce() + "\n" + loginEntity.getPassword();
		
		html.setOrignUrl(postLoginUrl);
		String sp = null;
		try {
			sp = rsaCrypt(loginEntity.getPubkey(), "10001", pwdString);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("entry", "weibo"));
		nvps.add(new BasicNameValuePair("gateway", "1"));
		nvps.add(new BasicNameValuePair("from", ""));
		nvps.add(new BasicNameValuePair("savestate", "7"));
		nvps.add(new BasicNameValuePair("useticket", "1"));
		nvps.add(new BasicNameValuePair("pagerefer", ""));
		nvps.add(new BasicNameValuePair("pcid", loginEntity.getPcid()));
		if (loginEntity.getShowpin() != null
				&& loginEntity.getShowpin().equals("1")
				&& loginEntity.getPicNO() != null) {
			nvps.add(new BasicNameValuePair("door", loginEntity.getPicNO()));
		}
		nvps.add(new BasicNameValuePair("vsnf", "1"));
		nvps.add(new BasicNameValuePair("su", loginEntity.getUsernameBase64()));
		nvps.add(new BasicNameValuePair("service", "miniblog"));
		nvps.add(new BasicNameValuePair("servertime", loginEntity.getServertime()));
		nvps.add(new BasicNameValuePair("nonce", loginEntity.getNonce()));
		nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
		nvps.add(new BasicNameValuePair("rsakv", loginEntity.getRsakv()));
		nvps.add(new BasicNameValuePair("sp", sp));
		nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
		nvps.add(new BasicNameValuePair("prelt", "115"));
		nvps.add(new BasicNameValuePair("returntype", "META"));
		nvps.add(new BasicNameValuePair("url","http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
		String entity = null;
		try {
			entity = new String(postRequest(html, nvps, getRedirectURL(), user.getUserAgent()), "gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (entity != null && entity.indexOf("retcode=0") > -1) {
			System.out.println(loginEntity.getUsername() + "\tlogin success.\r\n");
			
			String url = StringUtil.regMatcher(entity, "location.replace\\(['\"]", "['\"]").split("url=")[1].replace("%3A", ":").replace("%26", "&").replace("%3D", "=").replace("%2F", "/").replace("%3F", "?");
			html.setOrignUrl(url);
			html.setReferUrl(postLoginUrl);
			cookie(html, user.getUserAgent());
			System.out.println(cookie);
			user.setCookie(cookie);
			return true;
		} else {
			System.out.println(loginEntity.getUsername() + "\tlogin fail.");
			return false;
		}
	}
	
	private byte[] postRequest(HtmlInfo html, List<NameValuePair> form_data, String refererURL, String agent){
		HttpPost postMethod = new HttpPost(html.getOrignUrl());
		postMethod.addHeader("Content-Type", "application/x-www-form-urlencoded");
		postMethod.addHeader("Host", StringUtil.getHost(html.getOrignUrl()));
		postMethod.addHeader("User-Agent", agent==null?userAgent:agent);
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(form_data, html.getEncode()));
			HttpResponse response = httpClient(html).execute(postMethod);
			byte[] byteArray = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int count = -1;
			InputStream responseBodyAsStream;
			byte[] buffer = new byte[1024];
			if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				responseBodyAsStream = response.getEntity().getContent();
				Header contentEncodingHeader = response.getFirstHeader("Content-Encoding");  
		        if (contentEncodingHeader != null) {  
		            String contentEncoding = contentEncodingHeader.getValue();  
		            if (contentEncoding.toLowerCase(Locale.US).indexOf("gzip") != -1) {  
		            	responseBodyAsStream = new GZIPInputStream(responseBodyAsStream);  
		            }  
		        }  
		        Header contentType = response.getFirstHeader("Content-Type");  
		        if (contentType != null) {
		            String type = contentType.getValue();  
		            if (type.contains("charset")) {  
		            	String[] temp = type.split("charset=");
		            	if(type.length()>1) {
		            		type = temp[1].trim();
		            		html.setEncode(type);
		            	}
		            }
		        }  
				while ((count = responseBodyAsStream.read(buffer, 0, buffer.length)) > -1) {
					baos.write(buffer, 0, count);
				}
				buffer = null;
				baos.close();
				responseBodyAsStream.close();
				byteArray = baos.toByteArray();
			}
			return byteArray;
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try{
				postMethod.abort();
			}catch(Exception e){}		
		}
		return null;
	}
	@Override
	public boolean verify(UserAttr user) {
		String url = "http://weibo.com";
		HtmlInfo html = new HtmlInfo();
		html.setCookie(user.getCookie());
		html.setSite("sina");
		html.setOrignUrl(url);
		html.setEncode("utf-8");
		html.setType("LOGIN");
		getContent(html, user);
		String str = html.getContent();
		if(str.indexOf("我的首页") > -1 ||  str.indexOf("我的微博") > -1) {
			return true;
		}
		return false;
	}
	
	private String rsaCrypt(String modeHex, String exponentHex, String messageg)
			throws IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException,
			UnsupportedEncodingException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		BigInteger m = new BigInteger(modeHex, 16); /* public exponent */
		BigInteger e = new BigInteger(exponentHex, 16); /* modulus */
		RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
		RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
		Cipher enc = Cipher.getInstance("RSA");
		enc.init(Cipher.ENCRYPT_MODE, pub);
		byte[] encryptedContentKey = enc.doFinal(messageg.getBytes("GB2312"));
		return new String(Hex.encodeHex(encryptedContentKey));
	}
	//得到登录属性
	private SinaUserLoginEntity getEntity(UserAttr user) {
		SinaUserLoginEntity entity = new SinaUserLoginEntity();
		entity.setUsername(user.getName());
		entity.setPassword(user.getPass());
		String usernameBase64 = user.getName();
		if(usernameBase64.contains("@")) {
			usernameBase64 = usernameBase64.replaceFirst("@", "%40");
		}
		usernameBase64 = new String(EncoderUtil.getBase64Encode(usernameBase64.getBytes()));
		entity.setUsernameBase64(usernameBase64);
		String preloginurl = "http://login.sina.com.cn/sso/prelogin.php?entry=sso&callback=sinaSSOController.preloginCallBack&su="+ usernameBase64
				+ "&checkpin=1&rsakt=mod&client=ssologin.js(v1.4.5)&_=" + (new Date().getTime() / 1000);
		
		HtmlInfo html = new HtmlInfo();
		html.setOrignUrl(preloginurl);
		html.setType("LOGIN");
		html.setSite("sina");
		html.setEncode("utf-8");
		html.setReferUrl(null);
		getContent(html, user);
		
		String getResp = html.getContent();
		int firstLeftBracket = getResp.indexOf("(");
		int lastRightBracket = getResp.lastIndexOf(")");
		String firstJson = getResp.substring(firstLeftBracket + 1,lastRightBracket);
		JSONObject jsonInfo = JSONObject.fromObject(firstJson);
		entity.setNonce(jsonInfo.getString("nonce"));
		entity.setPcid(jsonInfo.getString("pcid"));
		entity.setPubkey(jsonInfo.getString("pubkey"));
		entity.setRetcode(jsonInfo.getString("retcode"));
		entity.setShowpin(jsonInfo.getString("showpin"));
		entity.setRsakv(jsonInfo.getString("rsakv"));
		entity.setServertime(jsonInfo.getString("servertime"));
		if((entity.getShowpin() != null && entity.getShowpin().equals("1"))){
			System.err.println("新浪微博登陆需要验证码，从validateImg文件夹查看验证码，将验证码写入相同名字的文本文件并复制到validateImg文件夹！");
			addPicDoor(entity);
		}
		return entity;
	}
	//需要验证码的情况，图片设置为文本读入验证码
	private void addPicDoor(SinaUserLoginEntity entity){
		String door = null;
		String validateImgPath = "validateImg";
		String picUrl = "http://login.sina.com.cn/cgi/pin.php?s=0&p=" + entity.getPcid() + "&r=" +  Math.round(Math.random()*8)+Math.round(Math.random()*8)+Math.round(Math.random()*8)+Math.round(Math.random()*8)+Math.round(Math.random()*8)+Math.round(Math.random()*8);
		String picName = entity.getUsername();
		HtmlInfo html = new HtmlInfo();
		html.setOrignUrl(picUrl);
		html.setSite("sina");
		html.setType("LOGIN");
		html.setEncode("utf-8");
		downloadVPic(html, validateImgPath, picName);
		
		String txt = validateImgPath + File.separator + picName + ".txt";
		while(door == null){
			File file = new File(txt);
			try {
				if(file.exists()){
					door = StringUtil.getContent(txt).replace("\n", "");
					door = door.replace(" ", "");
					door = door.replace("\r", "");
					door = door.replace("\t", "");
				}else{
					Thread.sleep(1000);
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}
		entity.setPicNO(door);
	}
	//需要验证码，下载到本地
	private boolean downloadVPic(HtmlInfo html, String picDir,String fileName) {
		HttpGet getMethod = null;
		try {
			String url = EncoderUtil.encodeKeyWords(html.getOrignUrl(), html.getEncode());
			getMethod = new HttpGet(url);
			getMethod.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
			getMethod.addHeader("User-Agent",userAgent);
			fileName = picDir + File.separator + fileName + ".jpg";
			File picFile = new File(fileName);
			HttpResponse response = httpClient(html).execute(getMethod);
			HttpEntity entity = response.getEntity();
			if(entity != null){
				// 得到网络资源的字节
				InputStream in = entity.getContent();
				BufferedImage img = ImageIO.read(in);
				in.close();
				if (img == null ) {
					return false;
				}
				if(!picFile.getParentFile().exists()){
					picFile.getParentFile().mkdirs();
				}
				AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(1, 1), null);
				BufferedImage new_img = op.filter(img, null);
				FileOutputStream out = new FileOutputStream(picFile);
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				encoder.encode(new_img);
				out.close();
				return true;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(getMethod != null)
				getMethod.abort();
		}
		return false;
	}
	
	class SinaUserLoginEntity {
		
		private String nonce;
		private String pcid;
		private String pubkey;
		private String retcode;
		private String showpin;
		private String rsakv;
		private String servertime;
		private String username;
		private String usernameBase64;
		private String password;
		private String picNO;
		
		public String getUsernameBase64() {
			return usernameBase64;
		}
		public void setUsernameBase64(String usernameBase64) {
			this.usernameBase64 = usernameBase64;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getPicNO() {
			return picNO;
		}
		public void setPicNO(String picNO) {
			this.picNO = picNO;
		}
		public String getNonce() {
			return nonce;
		}
		public void setNonce(String nonce) {
			this.nonce = nonce;
		}
		public String getPcid() {
			return pcid;
		}
		public void setPcid(String pcid) {
			this.pcid = pcid;
		}
		public String getPubkey() {
			return pubkey;
		}
		public void setPubkey(String pubkey) {
			this.pubkey = pubkey;
		}
		public String getRetcode() {
			return retcode;
		}
		public void setRetcode(String retcode) {
			this.retcode = retcode;
		}
		public String getServertime() {
			return servertime;
		}
		public void setServertime(String servertime) {
			this.servertime = servertime;
		}
		public String getShowpin() {
			return showpin;
		}
		public void setShowpin(String showpin) {
			this.showpin = showpin;
		}
		public String getRsakv() {
			return rsakv;
		}
		public void setRsakv(String rsakv) {
			this.rsakv = rsakv;
		}

	}
	private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
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
			boolean valid = verify(user);
			if(!valid) {
				Systemconfig.sysLog.log("cookie已失效，重新获取cookie！");
				login(user);
			} else {
				Systemconfig.sysLog.log("cookie仍有效！");
			}
		}
	}
}
