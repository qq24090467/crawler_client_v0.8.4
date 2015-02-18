package common.siteinfo;

import java.io.Serializable;
import java.util.Map;

/**
 * 站点属性结构
 * 
 * @author grs
 * @since 2013年1月
 */
@SuppressWarnings("serial")
public class Siteinfo implements Serializable {
	/** 站点名称，必须 */
	protected String siteName;
	/** 站点标识，必须 */
	protected int siteFlag;
	/** 站点编码，必须 */
	protected String charset;
	/** 采集间隔，必须 */
	protected int downInterval;
	/** 站点所属分类，需要，表示站点负责类型，用于快照文件归类 */
	protected String category;
	/** 站点采集使用的线程数量 */
	protected int threadNum;
	/** 采集入口url，必须 */
	protected String url;
	/** 采集入口urls，可选 */
	/** 采集页数，必须 */
	protected int page;
	/** 需要代理 */
	protected boolean agent;
	/** 是否需要登陆 */
	protected boolean login;
	/** 域名 */
	protected String hostDomain;// URL前缀
	/** 词:URL，URL为null表示共用url属性， 可选 */
	protected Map<String, String> keyUrl;
	/** 站点内的子模块， 可选 */
	protected Map<String, Siteinfo> subSite;
	/** 数据抽取组件，必须，只有元数据，详细数据，引文和被引数据组件四种类型 */
	protected Map<String, CommonComponent> commonComponent;
	private int cycleTime;

	@Override
	public String toString() {
		String tmp = "";
		tmp = "[siteName]" + siteName + 
				"[siteFlag]" + siteFlag + 
//				"[charset]" + charset + 
				"[downInterval]" + downInterval +
//				 "[category]" + category + 
				"[threadNum]" + threadNum + 
//				"[url]" + url + 
				"[page]" + page
//				+ "[agent]" + agent + "[login]" + login + "[hostDomain]" + hostDomain + "[keyUrl]" + keyUrl
//				+ "[subSite]" + subSite + "[commonComponent]" + commonComponent + "[cycleTime]" + cycleTime
				+"\r\n";
		return tmp;
	};

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public int getSiteFlag() {
		return siteFlag;
	}

	public void setSiteFlag(int siteFlag) {
		this.siteFlag = siteFlag;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getDownInterval() {
		return downInterval;
	}

	public void setDownInterval(int interval) {
		this.downInterval = interval;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getHostDomain() {
		return hostDomain;
	}

	public void setHostDomain(String hostDomain) {
		this.hostDomain = hostDomain;
	}

	public Map<String, String> getKeyUrl() {
		return keyUrl;
	}

	public void setKeyUrl(Map<String, String> keyUrl) {
		this.keyUrl = keyUrl;
	}

	public Map<String, Siteinfo> getSubSite() {
		return subSite;
	}

	public void setSubSite(Map<String, Siteinfo> subSite) {
		this.subSite = subSite;
	}

	public Map<String, CommonComponent> getCommonComponent() {
		return commonComponent;
	}

	public void setCommonComponent(Map<String, CommonComponent> commonComponent) {
		this.commonComponent = commonComponent;
	}

	public boolean getAgent() {
		return agent;
	}

	public void setAgent(boolean agent) {
		this.agent = agent;
	}

	public int getCycleTime() {
		return cycleTime;
	}

	public void setCycleTime(int cycleTime) {
		this.cycleTime = cycleTime;
	}

	public boolean getLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

}
