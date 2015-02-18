package common.extractor.xpath.news.monitor.sub;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.HtmlInfo;
import common.bean.NewsData;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.news.monitor.NewsMonitorExtractorAttribute;
import common.extractor.xpath.news.monitor.NewsMonitorXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * 抽取实现类
 * 
 * @author grs
 */
public class HcrubberMonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		String url = args[0];
		int count = 0;
		for (int i = 0; i < url.toCharArray().length; i++) {
			if (url.toCharArray()[i] == '-') {
				count++;
			}
		}

		
		String nextUrl="";
		if(count==0){//上一个是第1页
			nextUrl=url.replace(".shtml", "-2.shtml");
		}
		else {
			String currPageStr=url.substring(url.lastIndexOf("-")+1,url.lastIndexOf(".shtml"));
			
			int currPageInt = -1;
			try {
				currPageInt=Integer.parseInt(currPageStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			int nextPageInt=currPageInt+1;
			nextUrl=url.replace("-"+currPageStr+".shtml", "-"+nextPageInt+".shtml");
			
		}
		
		
		
		return nextUrl;
	}

	@Override
	public void parsePubtime1(List<NewsData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;

		for (int i = 0; i < nl.getLength(); i++) {
			String str=nl.item(0).getTextContent();
			str=str.replace("年", "-").replace("月", "-").replace("日", " ");
			list.get(i).setPubtime(str);
			list.get(i).setPubdate(timeProcess(list.get(i).getPubtime().trim()));
		}
	}
	
	@Override
	public void parseSource(NewsData data, Node dom, Component component, String... args) {
		String str = "";
		if (component == null)
			return;

		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;

		if (nl.item(0) != null)
			str = StringUtil.format(nl.item(0).getTextContent());
		str = str.replace("来源：", "");
		str = str.equals("")?"慧聪橡胶网":str;
		str = str.length()>20? "慧聪橡胶网" : str;
		data.setSource(str);
	}
}
