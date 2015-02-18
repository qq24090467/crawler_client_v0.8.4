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
public class CriaMonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {

	@Override
	public String parseNext(Node domtree, Component component, String... args) {
		if (component == null)
			return null;
		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl == null)
			return null;
		if (nl.item(0) != null) {
			String url = urlProcess(component, nl.item(0));
			String section = StringUtil.regMatcher(args[0], "http://", ".cria");

			if (section.equals("news"))
				;
			else if (section.equals("market")) {
				url = url.replace("news", "market");
			} else if (section.equals("www")) {
				url = url.replace("news", "www");
			}

			return url;
		}
		return null;
	}

	@Override
	public void parseUrl(List<NewsData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			String url = urlProcess(component, nl.item(i));
			String section = StringUtil.regMatcher(args[0], "http://", ".cria");
			if (section.equals("news"))
				;
			else if (section.equals("market")) {
				url = url.replace("news", "market");
			} else if (section.equals("www")) {
				url = url.replace("news", "www");
			}
			list.get(i).setUrl(url);
		}
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
		str = str.startsWith("注：") ? "中国橡胶网" : str;
		str = str.equals("") ? "中国橡胶网" : str;
		str = str.length() > 20 ? "中国橡胶网" : str;
		data.setSource(str);
	}
}
