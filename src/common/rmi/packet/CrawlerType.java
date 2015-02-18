package common.rmi.packet;

import java.util.HashMap;
import java.util.Map;

/**
 *  新闻搜索，新闻垂直，论坛搜索，论坛垂直，博客搜索，博客垂直，微博搜索，微博垂直，视频搜索，视频垂直，学术搜索，学术垂直
 *  电商搜索，电商垂直，微信搜索，微信垂直，相同新闻，单条微博
 *  
 *  专利搜索，报告搜索，基金搜索，电子报垂直
 * @author grs
 *
 */
public enum CrawlerType {
	NOTYPE(""),
	NEWS_SEARCH("1.1.1."), NEWS_MONITOR("2.1.1."),
	BBS_SEARCH("3.1.1."), BBS_MONITOR("4.1.1."),
	BLOG_SEARCH("5.1.1."), BLOG_MONITOR("6.1.1."),
	WEIBO_SEARCH("7.1.1."), WEIBO_MONITOR("8.1.1."),
	VIDEO_SEARCH("9.1.1."), VIDEO_MONITOR("10.1.1."),
	ACADEMIC_SEARCH("11.1.1."), ACADEMIC_MONITOR("12.1.1."),
	EBUSINESS_SEARCH("13.1.1."), EBUSINESS_MONITOR("14.1.1.1"),
	WEIXIN_SEARCH("15.1.1."), WEIXIN_MONITOR("16.1.1."),
	NEWS_SAME("17.1.1."), WEIBO_SINGLE("18.1.1."),
	
	PATENT_SEARCH("19.1.1."), EPAPER_MONITOR("20.1.1."),
	COMPANY_REPORT_SEARCH("21.1.1."),COMPANY_REPORT_MONITOR("22.1.1."),
	FUND_SEARCH("23.1.1."), 
	
	;
	private CrawlerType() {
		
	}
	private String code;
	private CrawlerType(String suffix) {
		this.code = suffix;
	}
	public String getCode() {
		return code;
	}
	
	public static CrawlerType getType(String type) {
		for(CrawlerType ct : CrawlerType.values()) {
			if(type.equalsIgnoreCase(ct.name())) {
				return ct;
			}
		}
		return NOTYPE;
	}
	
	private static Map<Integer, CrawlerType> map = new HashMap<Integer, CrawlerType>();
	static {
		map.put(0, NOTYPE);
		map.put(1, NEWS_SEARCH);	map.put(2, NEWS_MONITOR);
		map.put(3, BBS_SEARCH);map.put(4, BBS_MONITOR);
		map.put(5, BLOG_SEARCH);map.put(6, BLOG_MONITOR);
		map.put(7, WEIBO_SEARCH);map.put(8, WEIBO_MONITOR);
		map.put(9, VIDEO_SEARCH);	map.put(10, VIDEO_MONITOR);
		map.put(11, ACADEMIC_SEARCH);map.put(12, ACADEMIC_MONITOR);
		map.put(13, EBUSINESS_SEARCH);map.put(14, EBUSINESS_MONITOR);
		map.put(15, WEIXIN_SEARCH);map.put(16, WEIXIN_MONITOR);
		map.put(17, NEWS_SAME);//相同新闻	
		map.put(18, WEIBO_SINGLE);//单条微博采集
		map.put(19, PATENT_SEARCH);map.put(20, EPAPER_MONITOR);
		map.put(21, COMPANY_REPORT_SEARCH);map.put(22, COMPANY_REPORT_MONITOR);
		map.put(23, FUND_SEARCH);
	}
	public static Map<Integer, CrawlerType> getMap() {
		return map;
	}
}
