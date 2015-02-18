package common.bean;

/**
 * 网页信息
 * @author grs
 *
 */
public class HtmlInfo {

	private String site;//web站点
	private String orignUrl;//采集源链接——最终链接部分可能会不同
	private String realUrl;//最终链接，无变化为null
	private String encode;//页面编码
	private String content;//页面内容
	private boolean agent;//代理
	private String crawlerType;//采集的页面类型：列表页，内容页，引文页，被引页
	private boolean addHead;//更换请求头
	private String referUrl;//上一个页面链接
	private String cookie;//页面cookie
	private boolean fixEncode;
	private String fileType = ".htm";//文件类型
	private String ua;//user agent
	
	public String getUa() {
		return ua;
	}
	public void setUa(String ua) {
		this.ua = ua;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getOrignUrl() {
		return orignUrl;
	}
	public void setOrignUrl(String orignUrl) {
		this.orignUrl = orignUrl;
	}
	public String getRealUrl() {
		return realUrl;
	}
	public void setRealUrl(String realUrl) {
		this.realUrl = realUrl;
	}
	public String getEncode() {
		return encode;
	}
	public void setEncode(String encode) {
		this.encode = encode;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean getAgent() {
		return agent;
	}
	public void setAgent(boolean agent) {
		this.agent = agent;
	}
	public String getType() {
		return crawlerType;
	}
	public void setType(String type) {
		this.crawlerType = type;
	}
	public boolean getAddHead() {
		return addHead;
	}
	public void setAddHead(boolean addHead) {
		this.addHead = addHead;
	}
	public String getReferUrl() {
		return referUrl;
	}
	public void setReferUrl(String referUrl) {
		this.referUrl = referUrl;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public boolean getFixEncode() {
		return fixEncode;
	}
	public void setFixEncode(boolean changeEncode) {
		this.fixEncode = changeEncode;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
}
