package common.rmi.packet;

import java.io.Serializable;
import java.util.List;

/**
 * 配置项对象
 * @author grs
 * @since 2014年4月
 */
@SuppressWarnings("serial")
public class ConfigObject implements Serializable {
	
	private String name;//站点名
	private String destSite;//site文件夹的内容
	private String destConfig;//config/site文件夹的内容
	private String type;//部署类型
	private String sourceIP;//指定源IP
	private String destIP;//目标IP
	private int oper;//0修改爬虫IP，1添加，2修改，3删除
	
	private String crawler;//爬虫线程
	private List<SearchKey> keyObj;
	
	private int cycleTime;
	private int threadNum;
	private int interval;
	
	private int step;
	private String dataSource;//配置源，来自web还是crawler
	private String localIP;//本机IP
	private String tip;
//	private CrawlerType crawlerType;//爬虫类型

	public ConfigObject() {
	}
	public ConfigObject(String dataSource) {
		this.dataSource = dataSource;
	}
	public ConfigObject(String dataSource, String sourceIp) {
		this.dataSource = dataSource;
		this.localIP = sourceIp;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDestSite() {
		return destSite;
	}
	public void setDestSite(String destSite) {
		this.destSite = destSite;
	}
	public String getDestConfig() {
		return destConfig;
	}
	public void setDestConfig(String destConfig) {
		this.destConfig = destConfig;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSourceIP() {
		return sourceIP;
	}
	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}
	public String getDestIP() {
		return destIP;
	}
	public void setDestIP(String destIP) {
		this.destIP = destIP;
	}
	public int getOper() {
		return oper;
	}
	public void setOper(int oper) {
		this.oper = oper;
	}
	public int getCycleTime() {
		return cycleTime;
	}
	public void setCycleTime(int cycleTime) {
		this.cycleTime = cycleTime;
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
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getLocalIP() {
		return localIP;
	}
	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}
	public String getCrawler() {
		return crawler;
	}
	public void setCrawler(String crawler) {
		this.crawler = crawler;
	}
	public List<SearchKey> getKeyObj() {
		return keyObj;
	}
	public void setKeyObj(List<SearchKey> keyObj) {
		this.keyObj = keyObj;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
//	public CrawlerType getCrawlerType() {
//		return crawlerType;
//	}
//	public void setCrawlerType(CrawlerType crawlerType) {
//		this.crawlerType = crawlerType;
//	}
	
	public void set(ConfigObject cf) {
		this.name = cf.getName();
		this.destConfig = cf.getDestConfig();
		this.destSite = cf.getDestSite();
		this.destIP = cf.getDestIP();
		this.sourceIP = cf.getSourceIP();
		this.type = cf.getType();
		this.oper = cf.getOper();
		this.cycleTime = cf.getCycleTime();
		this.threadNum = cf.getThreadNum();
		this.interval = cf.getInterval();
		this.step = cf.getStep();
		this.keyObj = cf.getKeyObj();
		this.tip = cf.getTip();
//		this.crawlerType = cf.getCrawlerType();
	}
	
	public void reset() {
		this.name = null;
		this.destConfig = null;
		this.destSite = null;
		this.destIP = null;
		this.sourceIP = null;
		this.type = null;
		this.oper = 0;
		this.step = 0;
		this.keyObj = null;
		this.tip = null;
//		this.crawlerType = null;
	}
	
}
