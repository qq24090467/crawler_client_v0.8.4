package common.rmi.packet;

import java.io.Serializable;
/**
 * 
 * @author grs
 * @since 2014年8月
 */
@SuppressWarnings("serial")
public class BanlanceData implements Serializable {
	private byte[] info = new byte[2];//client信息
	//区间内数据
	private int dataStart;
	private int dataEnd;
	private int siteStart;
	private int siteEnd;

	public byte[] getInfo() {
		return info;
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
	public int getDataStart() {
		return dataStart;
	}
	public void setDataStart(int dataStart) {
		this.dataStart = dataStart;
	}
	public int getDataEnd() {
		return dataEnd;
	}
	public void setDataEnd(int dataEnd) {
		this.dataEnd = dataEnd;
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
	
}