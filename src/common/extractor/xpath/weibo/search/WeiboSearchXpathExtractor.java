package common.extractor.xpath.weibo.search;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.HtmlInfo;
import common.bean.WeiboData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * 微博抽取实现类
 * @author grs
 */
public class WeiboSearchXpathExtractor extends XpathExtractor<WeiboData> implements WeiboSearchExtractorAttribute {
	@Override
	public void parseUrl(List<WeiboData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(0)));
		}
	}
	@Override
	public void parseTitle(List<WeiboData> list, Node dom, Component component, String... args) {
	}
	@Override
	public String parseNext(Node dom, Component component, String... args) {
		if(component==null) return null;
		NodeList nl = head(component.getXpath(), dom);
		if(nl == null) return null;
		if(nl.item(0)!=null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}
	/**
	 * 摘要
	 * @param list
	 * @param dom
	 * @param component
	 * @param strings
	 */
	@Override
	public void parseContent(List<WeiboData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String url = nl.item(i).getTextContent();
			list.get(i).setContent(url==null?"":url);
		}
	}
	/**
	 * 来源
	 * @param list
	 * @param dom
	 * @param component
	 * @param strings
	 */
	@Override
	public void parseSource(List<WeiboData> list, Node dom, Component component, String... strings) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setSource(nl.item(i).getTextContent());
		}
	}
	/**
	 * 发布时间
	 * @param list
	 * @param dom
	 * @param component
	 * @param strings
	 */
	@Override
	public void parsePubtime(List<WeiboData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent());
			list.get(i).setPubdate(timeProcess(list.get(i).getPubtime().trim()));
		}
	}
	@Override
	public void processList(List<WeiboData> list, Node domtree,
			Map<String, Component> comp, String... args) {
		this.parseAuthor(list, domtree, comp.get("author"));
		
		if (list.size() == 0) return;
		
		this.parseUrl(list, domtree, comp.get("url"));
		this.parseContent(list, domtree, comp.get("content"));
//		this.parseAuthorImg(list, domtree, comp.get("author_img"));
		this.parsePubtime(list, domtree, comp.get("pubtime"));
		this.parseAuthorUrl(list, domtree, comp.get("author_url"));
		this.parseImgUrl(list, domtree, comp.get("img_url"));
		this.parseRttNum(list, domtree, comp.get("rtt_num"));
		this.parseCommentNum(list, domtree, comp.get("comment_num"));
		this.parseMid(list, domtree, comp.get("mid"));
//		this.parseRttContent(list, domtree, comp.get("rtt_content"));
		this.parseUid(list, domtree, comp.get("uid"));
		parseSource(list, domtree, comp.get("source"));
		this.parseCommenturl(list, domtree, null);
		this.parseRtturl(list, domtree, null);
	}
	
	@Override
	public String templateContentPage(WeiboData data, HtmlInfo html, int page,
			String... keyword) {
		return null;
	}
	@Override
	public void parseCommenturl(List<WeiboData> list, Node dom,
			Component component, String... args) {
	}
	@Override
	public void parseRtturl(List<WeiboData> list, Node dom,
			Component component, String... args) {
	}
	@Override
	public void parseUid(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		
	}
	@Override
	public void parseImgUrl(List<WeiboData> list, Node dom,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String url = nl.item(i).getTextContent();
			list.get(i).setImgUrl(url);
		}
	}
	@Override
	public void parseMid(List<WeiboData> list, Node dom, Component component) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setMid(nl.item(i).getTextContent().trim());
		}
	}
	@Override
	public void parseRttContent(List<WeiboData> list, Node dom,
			Component component, String... content) {
	}
	
	@Override
	public void parseCommentNum(List<WeiboData> list, Node dom,
			Component component) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String s = StringUtil.extrator(nl.item(i).getTextContent(), "\\d+");
			if(s.equals("")) 
				list.get(i).setCommentNum(0);
			else
				list.get(i).setCommentNum(Integer.parseInt(s));
		}
	}
	@Override
	public void parseRttNum(List<WeiboData> list, Node dom,
			Component component) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String s = StringUtil.extrator(nl.item(i).getTextContent(), "\\d+");
			if(s.equals("")) 
				list.get(i).setRttNum(0);
			else
				list.get(i).setRttNum(Integer.parseInt(s));
		}
	}
	@Override
	public void parseAuthorUrl(List<WeiboData> list, Node dom,
			Component component, String... content) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setAuthorurl(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseAuthorImg(List<WeiboData> list, Node dom,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setAuthorImg(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseAuthor(List<WeiboData> list, Node dom,
			Component component) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom);
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			WeiboData vd = new WeiboData();
			vd.setAuthor(nl.item(i).getTextContent());
			list.add(vd);
		}
	}
	@Override
	public void processPage(WeiboData data, Node domtree,
			Map<String, Component> map, String... args) {
	}
	
}
	