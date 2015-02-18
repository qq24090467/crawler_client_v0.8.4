package common.extractor.xpath.news.monitor.sub;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
public class TyrefhMonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {

	@Override
	public void parsePubtime1(List<NewsData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;

		for (int i = 0; i < nl.getLength(); i++) {
			String str = nl.item(0).getTextContent();
			str = str.replace("[", "").replace("]", "").replace("/", "-");
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

		if (nl.item(0) != null) {
			str = nl.item(0).getTextContent();
			str = StringUtil.format(str);
		}
		if (str.contains("来源："))
			str = str.substring(str.indexOf("来源：") + 3);
		if (str.contains("发布日期"))
			str = str.substring(0, str.indexOf("发布日期")).replace(" ", "").trim();
		if (str == null)
			str = "中国橡胶工业协会-轮胎分会";
		else {
			str = str.contains("本站") ? "中国橡胶工业协会-轮胎分会" : str;
		}
		str = str.length()>20? "中国橡胶工业协会-轮胎分会" : str;
		data.setSource(str);
	}

	@Override
	public void parseAuthor(NewsData data, Node dom, Component component, String... args) {
		String str = "";
		if (component == null) {
			return;
		}
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null) {
			return;
		}
		if (nl.item(0) != null)
			str = StringUtil.format(nl.item(0).getTextContent());
		str = str.replace("●", "").trim();

		data.setSource(str);
	}

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		String url = args[0];

		String currPageStr = url.substring(url.lastIndexOf("page_index=") + 11);
		int currPageInt = -1;
		try {
			currPageInt = Integer.parseInt(currPageStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nextPageInt = currPageInt + 1;
		String nextUrl = url.replace("page_index=" + currPageStr, "page_index=" + nextPageInt);
		return nextUrl;
	}
}
