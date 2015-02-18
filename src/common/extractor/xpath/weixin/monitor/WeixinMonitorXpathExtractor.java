package common.extractor.xpath.weixin.monitor;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.text.html.HTML;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.CommonData;
import common.bean.HtmlInfo;
import common.bean.WeixinData;
import common.extractor.xpath.XpathExtractor;
import common.http.SimpleHttpProcess;
import common.siteinfo.Component;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;
import common.util.TimeUtil;

/**
 * 抽取实现类
 * 
 * @author gxd
 */
public class WeixinMonitorXpathExtractor extends XpathExtractor<WeixinData> implements WeixinMonitorExtractorAttribute {

	@Override
	public void processPage(WeixinData data, Node domtree, Map<String, Component> comp, String... args) {
		this.parseSource(data, domtree, comp.get("source"));
		this.parsePubtime(data, domtree, comp.get("pubtime"));
		this.parseAuthor(data, domtree, comp.get("author"));
		this.parseContent(data, domtree, comp.get("content"));
		this.parseImgUrl(data, domtree, comp.get("imgs_url"));
//		TimeUtil.rest(10);
//		this.parseNumber(data, domtree, comp.get(""));
	}

	public void parseNumber(WeixinData data, Node dom, Component component, String... args) {
		// http://mp.weixin.qq.com/s?__biz=MjM5ODE1NTMxMQ==&mid=201653867&idx=1&sn=6f3445a3640eb09ce7cfa5a49509f165&3rd=MzA3MDU4NTYzMw==&scene=6#rd

		String biz = "";
		String mid = "";
		String uin = "";
		String key = "";
		String fromFile = StringUtil.getContent("config/WeixinKey/WeixinKey.txt");
		try {
			biz = StringUtil.regMatcher(data.getUrl(), "__biz=", "&");
			mid = StringUtil.regMatcher(data.getUrl(), "mid=", "&");
			for (String string : fromFile.split("&")) {
				if (string.contains("uin"))
					uin = string.split("=")[1].trim();
				if (string.contains("key"))
					key = string.split("=")[1].trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String url = "http://mp.weixin.qq.com" + "/mp/getappmsgext?" + "__biz=" + biz + "&mid=" + mid + "&uin=" + uin
				+ "&key=" + key
				// +
				// "&pass_ticket=b3hV91xTLYZxRGKemRNz%2FAi4VKElPnwHYUNtoV8w4dE%3D"

				+ "";

		HtmlInfo html = new HtmlInfo();

		String charSet = "UTF-8";
		html.setType("DATA");
		html.setEncode(charSet);
		html.setOrignUrl(url);
		html.setCookie("Set-Cookie: wxuin=20156425; Path=/; Expires=Fri, 02-Jan-1970 00:00:00 GMT");
		html.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
		SimpleHttpProcess shp = new SimpleHttpProcess();
		shp.getContent(html);
		String content = html.getContent();

		int retry = 0;
		while (!content.contains("read_num")) {
			if (retry++ > 3)
				break;
			Systemconfig.sysLog.log("请获取key后输入任意内容回车继续...输入c忽略(很可能无法继续采集，不推荐)");
			System.err.println("请获取key后输入任意内容回车继续...输入c忽略(很可能无法继续采集，不推荐)");
			Scanner input = new Scanner(System.in);
			String s = input.next();
			if (s.equals("c") || s.equals("C"))
				break;

			fromFile = StringUtil.getContent("config/WeixinKey/WeixinKey.txt");
			try {
				for (String string : fromFile.split("&")) {
					if (string.contains("uin"))
						uin = string.split("=")[1].trim();
					if (string.contains("key"))
						key = string.split("=")[1].trim();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			url = "http://mp.weixin.qq.com" + "/mp/getappmsgext?" + "__biz=" + biz + "&mid=" + mid + "&uin=" + uin
					+ "&key=" + key;
			html = new HtmlInfo();

			charSet = "UTF-8";
			html.setType("DATA");
			html.setEncode(charSet);
			html.setOrignUrl(url);
			html.setCookie("Set-Cookie: wxuin=20156425; Path=/; Expires=Fri, 02-Jan-1970 00:00:00 GMT");
			html.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
			shp = new SimpleHttpProcess();
			shp.getContent(html);
			content = html.getContent();
		}

		String readNumStr = StringUtil.regMatcher(content, "\"read_num\":", ",");
		String praiseNumStr = StringUtil.regMatcher(content, "\"like_num\":", ",");

		try {
			if (readNumStr != null)
				data.setReadNum(Integer.parseInt(readNumStr));

			if (praiseNumStr != null)
				data.setPraiseNum(Integer.parseInt(praiseNumStr));

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	public void parseImgUrl(WeixinData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String imgs = "";
		for (int i = 0; i < nl.getLength(); i++) {
			imgs += StringUtil.format(nl.item(i).getTextContent()) + ";";
		}
		data.setImgUrl(imgs);
	}

	@Override
	public void parseAuthor(WeixinData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setAuthor(StringUtil.format(nl.item(0).getTextContent()));
	}

	@Override
	public void processList(List<WeixinData> list, Node domtree, Map<String, Component> comp, String... args) {
		String content = args[0];
		this.parseTitle(list, domtree, comp.get("title"), content);

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, comp.get("url"), content);

		this.parseBrief(list, domtree, comp.get("brief"), content);

	}

	@Override
	public void parseUrl(List<WeixinData> list, Node dom, Component component, String... args) {
		if (args[0] == null || args[0] == "")
			return;
		List<String> results = StringUtil.regMatches(args[0], "<url>", "/url", true);
		for (int i = 0; i < results.size(); i++) {

			String tmp = results.get(i);
			String result = StringUtil.regMatcher(tmp, "CDATA\\[", "\\]");
			list.get(i).setUrl(result);
		}
	}

	@Override
	public void parseTitle(List<WeixinData> list, Node dom, Component component, String... args) {
		if (args[0] == null || args[0] == "")
			return;
		List<String> results = StringUtil.regMatches(args[0], "title>", "/title", true);
		for (int i = 0; i < results.size(); i++) {

			String tmp = results.get(i);
			String result = StringUtil.regMatcher(tmp, "CDATA\\[", "\\]");
			WeixinData vd = new WeixinData();
			vd.setTitle(result);
			list.add(vd);
		}
	}

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		String currUrl = args[0];
		String currPageStr = currUrl.substring(currUrl.lastIndexOf("page=") + 5);

		int currPageInt = currPageStr != null ? Integer.parseInt(currPageStr) : -1;

		if (currPageInt == -1)
			return null;
		int nextPageInt = currPageInt + 1;
		String nextUrl = currUrl.replace("page=" + currPageStr, "page=" + nextPageInt);

		return nextUrl;
	}

	/**
	 * 摘要
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param strings
	 */
	@Override
	public void parseBrief(List<WeixinData> list, Node dom, Component component, String... args) {
		if (args[0] == null || args[0] == "")
			return;
		List<String> results = StringUtil.regMatches(args[0], "content168>", "/content168", true);
		for (int i = 0; i < results.size(); i++) {

			String tmp = results.get(i);
			String result = StringUtil.regMatcher(tmp, "CDATA\\[", "\\]");
			list.get(i).setBrief(result);
		}
	}

	/**
	 * 来源
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param strings
	 */
	@Override
	public void parseSource(List<WeixinData> list, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setSource(StringUtil.format(nl.item(i).getTextContent()));
		}
	}

	@Override
	public void parsePubtime(WeixinData data, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null) {
			data.setPubtime(nl.item(0).getTextContent());
			data.setPubdate(timeProcess(data.getPubtime().trim()));
		}
	}

	@Override
	public void parseSource(WeixinData data, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		if (nl.item(0) != null)
			data.setSource(StringUtil.format(nl.item(0).getTextContent()));
	}

	@Override
	public void parseContent(WeixinData data, Node dom, Component component, String... strings) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		String str = "";
		for (int i = 0; i < nl.getLength(); i++) {
			if (i < nl.getLength() - 1)
				str += nl.item(i).getTextContent() + "\r\n";
			else
				str += nl.item(0).getTextContent();
		}
		data.setContent(str);
	}

	/**
	 * 共有属性设置
	 * 
	 * @param list
	 * @param siteflag
	 * @param key
	 * @param code
	 */
	@Override
	protected void attrSet(List<WeixinData> list, int siteflag, String key, int code) {
		for (WeixinData data : list) {
			WeixinData cd = (WeixinData) data;
			cd.setSearchKey(key);
			cd.setCategoryCode(code);
			cd.setMd5(MD5Util.MD5(cd.getUrl()) + Systemconfig.crawlerType);
			cd.setSiteId(siteflag);
		}
	}

}
