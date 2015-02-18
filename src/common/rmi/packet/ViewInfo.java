package common.rmi.packet;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class ViewInfo implements Serializable {
	private int buildType;//部署类型，新闻论坛博客微博视频学术
	private int style;//采集方式：0垂直/1搜索
	private String ip;//站点位于的IP
	private String name;//站点名
	
	private String file;//配置文件内容
	//KEY爬虫:VALUE搜索词OR地址
	private HashMap<String, InnerInfo> crawlers;//爬虫
	private String recomment;//备注
	private int threadNum;//线程数
	private int interval;//间隔
	private int crawlerCycle;//采集周期
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getBuildType() {
		return buildType;
	}
	public void setBuildType(int type) {
		this.buildType = type;
	}
	public int getStyle() {
		return style;
	}
	public void setStyle(int style) {
		this.style = style;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public HashMap<String, InnerInfo> getCrawlers() {
		return crawlers;
	}
	public void setCrawlers(HashMap<String, InnerInfo> crawlers) {
		this.crawlers = crawlers;
	}
	public String getRecomment() {
		return recomment;
	}
	public void setRecomment(String recomment) {
		this.recomment = recomment;
	}
	public int getThreadNum() {
		return threadNum;
	}
	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getCrawlerCycle() {
		return crawlerCycle;
	}
	public void setCrawlerCycle(int crawlerCycle) {
		this.crawlerCycle = crawlerCycle;
	}
	
	public class InnerInfo implements Serializable {
		private SearchKey searchKey;//爬虫属性信息
		private String account;//账户信息
		private String accountTip;//账户提示
		private int accountId;
		private int alive;//存活状态
		
		public SearchKey getSearchKey() {
			return searchKey;
		}
		public void setSearchKey(SearchKey key) {
			this.searchKey = key;
		}
		public String getAccount() {
			return account;
		}
		public void setAccount(String account) {
			this.account = account;
		}
		public String getAccountTip() {
			return accountTip;
		}
		public void setAccountTip(String accountTip) {
			this.accountTip = accountTip;
		}
		public int getAccountId() {
			return accountId;
		}
		public void setAccountId(int accountId) {
			this.accountId = accountId;
		}
		public int getAlive() {
			return alive;
		}
		public void setAlive(int alive) {
			this.alive = alive;
		}
	}
}
