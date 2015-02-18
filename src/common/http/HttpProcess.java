package common.http;

import org.apache.http.client.HttpClient;

import common.bean.HtmlInfo;
import common.system.UserAttr;

public interface HttpProcess {

	/**
	 * 获得请求页面内容
	 * @param html
	 */
	public void getContent(HtmlInfo html);
	
	/**
	 * 根据网页信息设置httpclient请求参数
	 * @param html
	 * @return
	 */
	public HttpClient httpClient(HtmlInfo html);
	
	/**
	 * 需要登录用户的页面内容
	 * @param html
	 * @param user
	 */
	public void getContent(HtmlInfo html, UserAttr user);
	
	/**
	 * 登陆状态检测
	 * @param userAttr 
	 */
	public void monitorLogin(UserAttr userAttr);
	
	public String getJsonContent(String ownerInitUrl);
}
