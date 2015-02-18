package common.system;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserAttr implements Serializable {

	private int id;
	private String ip;
	private String siteFlag;//用户负责站点
	private String name;//用户登录名
	private String pass;//密码
	private String cookie;//cookie
	private String userAgent;//模拟不同浏览器
	private int agentIndex;//不同agent的索引
	private int stat;//有效,被使用
	private String tip;
	private String referer;
	private boolean hadRun;//是否曾经运行过
	
	public UserAttr() {
	}
	public UserAttr(String user, String pass) {
		this.name = user;
		this.pass = pass;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getSiteFlag() {
		return siteFlag;
	}
	public void setSiteFlag(String string) {
		this.siteFlag = string;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public int getUsed() {
		return stat;
	}
	public void setUsed(int used) {
		this.stat = used;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	public boolean getHadRun() {
		return hadRun;
	}
	public void setHadRun(boolean hadRun) {
		this.hadRun = hadRun;
	}
	public int getAgentIndex() {
		return agentIndex;
	}
	public void setAgentIndex(int agentIndex) {
		this.agentIndex = agentIndex;
	}
	
}
