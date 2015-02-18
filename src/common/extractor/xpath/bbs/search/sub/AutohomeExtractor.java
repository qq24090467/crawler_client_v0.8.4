package common.extractor.xpath.bbs.search.sub;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.BBSData;
import common.bean.HtmlInfo;
import common.bean.ReplyData;
import common.extractor.xpath.bbs.search.BbsSearchXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;

public class AutohomeExtractor extends BbsSearchXpathExtractor {

	@Override
	public void processList(List<BBSData> list, Node domtree, Map<String, Component> components, String... args) {
		this.parseTitle(list, domtree, components.get("title"));

		if (list.size() == 0)
			return;

		this.parseUrl(list, domtree, components.get("url"));
		this.parseAuthor(list, domtree, components.get("author_lp"));
		this.parsePubtime(list, domtree, components.get("pubtime_lp"));
		this.parseCommentCount(list, domtree, components.get("comment_count_lp"));
		// this.parseClickCount(list, domtree,
		// components.get("click_count_lp"));
		this.parseBrief(list, domtree, components.get("brief_lp"));

	}

	public void parseBrief(List<BBSData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		// System.err.println(nl.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setBrief(nl.item(i).getTextContent());
		}
	}

	public void parseAuthor(List<BBSData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		// System.out.println("作者数："+nl.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setAuthor(nl.item(i).getTextContent().replace("楼主：", ""));
		}
	}

	public void parsePubtime(List<BBSData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent().replace("发表于：", ""));
		}
	}

	public void parseCommentCount(List<BBSData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		// System.out.println("评论数数："+nl.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setReplyCount(Integer.parseInt(nl.item(i).getTextContent().replace(" 个回复", "")));
		}
	}

	public void parseClickCount(List<BBSData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null)
			return;
		// System.out.println("点击数数："+nl.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i)
					.setClickCount(Integer.parseInt(nl.item(i).getTextContent().replace("浏览(", "").replace(")", "")));
		}
	}

	@Override
	public void parseReplytime(List<ReplyData> list, Node dom, Component component, String... strings) {
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		judge(list.size(), nl.getLength(), "replytime");
		for (int i = 0; i < nl.getLength(); i++) {
			// JSONObject jo =
			// JSONObject.fromObject(nl.item(i).getTextContent());
			// jo = jo.getJSONObject("content");
			// list.get(i).setTime(jo.getString("date"));
			String text = nl.item(i).getTextContent().replace("\t", "").replace("\n", "").replace("\r\n", "").trim();
			list.get(i).setPubdate(timeProcess(text.split(" ")[0]));
		}
	}

	@Override
	public void parseReplyCount(BBSData data, Node domtree, Component component, String... ags) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl == null)
			return;
		if (nl.item(0) != null) {
			String time = StringUtil.extrator(nl.item(0).getTextContent().split("回复")[0], "\\d");
			if (time == null || time.equals(""))
				data.setReplyCount(0);
			else
				data.setReplyCount(Integer.parseInt(time));
		}
	}

	@Override
	public String templateContentPage(BBSData data, HtmlInfo html, int page, String... keyword) {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		// create(content);
		// StringUtil.writeFile("c:/a.htm", html.getContent());
		// System.out.println(html.getContent());
		if (page != 1)
			System.out.println();
		Node domtree = getRealDOM(html);
		if (domtree == null) {
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType()
				.substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件
		if (page == 1) {
			this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
			this.parseClickCount(data, domtree, comp.getComponents().get("click_count"), html.getContent());
			this.parseImgUrl(data, domtree, comp.getComponents().get("img_url"), html.getContent());
			if (data.getPubdate() == null && data.getPubtime() != null)
				data.setPubdate(timeProcess(data.getPubtime().trim()));

			data.setInserttime(new Date());
			data.setSiteId(siteinfo.getSiteFlag());
			if (data.getMd5() == null)
				data.setMd5(MD5Util.MD5(data.getUrl()));

			if (data.getTitle() == null) {
				this.parsePageTitle(data, domtree, comp.getComponents().get("pageTitle"), html.getContent());
			}
		}

		// 回复列表
//		if (page == 1)
//			System.out.print("");
		List<ReplyData> list = new ArrayList<ReplyData>();
//		this.parseReplyname(list, domtree, comp.getComponents().get("reply_name"), new String[] { html.getContent() });
//		
//		if (list.size() > 0) {
//			this.parseReplytime(list, domtree, comp.getComponents().get("reply_time"),
//					new String[] { html.getContent() });
//			this.parseReplycontent(list, domtree, comp.getComponents().get("reply_content"),
//					new String[] { html.getContent() });
//			Systemconfig.sysLog.log("评论页解析完成!" + data.getUrl());
//		} else {
//			Systemconfig.sysLog.log("评论页解析为空!" + data.getUrl());
//		}

		data.setReplyList(list);
		
		String next = this.parseReplyNext(domtree, comp.getComponents().get("reply_next"));
		domtree = null;
		return next;
	}
}
