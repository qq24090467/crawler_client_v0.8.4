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
public class Tire21MonitorExtractor extends NewsMonitorXpathExtractor implements NewsMonitorExtractorAttribute {

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		String url = args[0];
		int count = 0;
		for (int i = 0; i < url.toCharArray().length; i++) {
			if (url.toCharArray()[i] == '-') {
				count++;
			}
		}

		int currPageInt = 0;
		if (count != 1) {
			String currPageStr = url.substring(url.lastIndexOf("-") + 1, url.lastIndexOf(".htm"));

			try {
				currPageInt = Integer.parseInt(currPageStr);
			} catch (Exception e) {
				System.out.println("截取到非数字字符.");
				e.printStackTrace();
			}
		}
		int nextPage = currPageInt + 1;

		if (count == 1) {
			url = url.substring(0, url.lastIndexOf(".htm")) + "-" + nextPage + url.substring(url.lastIndexOf(".htm"));
		} else {
			url = url.replace("-" + currPageInt + ".htm", "-" + nextPage + ".htm");
		}

		return url;
	}

	@Override
	public void parseSource(NewsData data, Node dom, Component component, String... args) {
		data.setSource("中国轮胎网");
	}
}
