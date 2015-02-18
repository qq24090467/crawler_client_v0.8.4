package common.http;

import common.system.UserAttr;

/**
 * 需要cookie的http请求处理
 * @author grs
 * @since 2014年8月
 */
public abstract class NeedCookieHttpProcess extends SimpleHttpProcess {
	protected String cookie = "";
//	protected UserAttr user;
	
	public abstract boolean login(UserAttr user) ;
	
	public abstract boolean verify(UserAttr user);
	
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getCookie() {
		return cookie;
	}
//	public UserAttr getUser() {
//		return user;
//	}
//	public void setUser(UserAttr user) {
//		this.user = user;
//	}
//	
}
