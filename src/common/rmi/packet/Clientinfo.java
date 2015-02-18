package common.rmi.packet;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
/**
 * client结构信息
 * @author grs
 * @since 2014年8月
 */
@SuppressWarnings("serial")
public class Clientinfo implements Serializable {
	private byte[] info = new byte[2];//client信息
	private String clientName;
	/* 客户端负载值 */
	private int load;
	/* 地址 */
	private InetAddress clientaddress;
	/* 心跳时间 */
	private long time;
	/* client上运行的爬虫信息 */
	private Map<String, ViewInfo> viewinfos;
	/* client存活状态 */
	private boolean valid;
	//区间内数据
	private int dataStart;
	private int dataEnd;
	private int siteStart;
	private int siteEnd;

	public byte[] getInfo() {
		return info;
	}
	//获得client的爬虫拼接串
	public String getClientName() {
		if(info.length > 2)//暂不实现
			return null;
		else {
			clientName = CrawlerType.getMap().get((int)info[0]).getCode() + info[1];
			return clientName;
		}
	}
	//获得client的爬虫类型
	public CrawlerType getType() {
		return CrawlerType.getMap().get((int)info[0]);
	}
	//client所属的类型
	public int getTypeNum() {
		return (int)info[0];
	}
	//client在所属类型中的索引
	public int index() {
		return info[1];
	}
	public InetAddress getClientaddress() {
		return clientaddress;
	}
	public void setClientaddress(InetAddress clientaddress) {
		this.clientaddress = clientaddress;
	}
	public int getLoad() {
		return load;
	}
	public void setLoad(int load) {
		this.load = load;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public boolean getValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public int getDataStart() {
		return dataStart;
	}
	public void setDataStart(int start) {
		this.dataStart = start;
	}
	public int getDataEnd() {
		return dataEnd;
	}
	public void setDataEnd(int end) {
		this.dataEnd = end;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public int getSiteStart() {
		return siteStart;
	}
	public void setSiteStart(int siteStart) {
		this.siteStart = siteStart;
	}
	public int getSiteEnd() {
		return siteEnd;
	}
	public void setSiteEnd(int siteEnd) {
		this.siteEnd = siteEnd;
	}
	public Map<String, ViewInfo> getViewinfos() {
		if(viewinfos==null)
			viewinfos = new HashMap<String, ViewInfo>();
		return viewinfos;
	}
	
}