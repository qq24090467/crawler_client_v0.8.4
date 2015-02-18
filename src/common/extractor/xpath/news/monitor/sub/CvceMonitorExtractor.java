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
import common.rmi.packet.SearchKey;
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
public class CvceMonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {

	protected String urlProcess(Component component, Node nl, String prefix) {
		String url = nl.getTextContent().replace("./", "");
		return prefix + url;
	}

	@Override
	public void parseUrl(List<NewsData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i), args[0]));
		}
	}

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		String url = args[0];
		int count = 0;
		for (int i = 0; i < url.toCharArray().length; i++) {
			if (url.toCharArray()[i] == '_') {
				count++;
			}
		}

		String nextUrl = "";
		if (count == 0) {// 上一个是第1页
			nextUrl = url + "index_1.shtml";
		} else {
			String currPageStr = url.substring(url.lastIndexOf("_") + 1, url.lastIndexOf(".shtml"));

			int currPageInt = -1;
			try {
				currPageInt = Integer.parseInt(currPageStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			int nextPageInt = currPageInt + 1;
			nextUrl = url.replace("_" + currPageStr + ".shtml", "_" + nextPageInt + ".shtml");

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
			String str = nl.item(0).getTextContent();
			str = str.replace("[", "").replace("]", "");
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
		str = str.equals("")?"中国商用汽车网":str;
		str = str.length()>20? "中国商用汽车网" : str;
		data.setSource(str);
	}
}
