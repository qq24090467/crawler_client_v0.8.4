package common.extractor.xpath.blog.search;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.HtmlInfo;
import common.bean.BlogData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.ExtractResult;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * 抽取实现类
 * @author grs
 */
public class BlogSearchXpathExtractor extends XpathExtractor<BlogData> implements BlogSearchExtractorAttribute {
	@Override
	public void parseUrl(List<BlogData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i)));
		}
	}
	@Override
	public void parseTitle(List<BlogData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom);
		for(int i = 0;i < nl.getLength();i++) {
			BlogData vd = new BlogData();
			vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			list.add(vd);
		}
	}
	@Override
	public String parseNext(Node dom, Component component, String... args) {
		if(component==null) return null;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return null;
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
	public void parseBrief(List<BlogData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setBrief(nl.item(i).getTextContent());
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
	public void parseSource(List<BlogData> list, Node dom, Component component, String... strings) {
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
	public void parsePubtime(List<BlogData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent());
			list.get(i).setPubdate(timeProcess(list.get(i).getPubtime().trim()));
		}
	}
	
	@Override
	public void parseAuthor(List<BlogData> list, Node dom,
			Component component, String... content) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setBlogAuthor(nl.item(i).getTextContent());
		}
	}
	
	@Override
	public String templateContentPage(BlogData data, HtmlInfo html, int page,
			String... keyword) {
		ExtractResult result = Systemconfig.extractor.extract(html.getContent(), html.getEncode(),data.getUrl());
		String title = data.getTitle()==null?result.getTitle():data.getTitle();
		//标题需要处理
		//搜索词里面带有特殊符号的情况
		boolean spe = false;
		if(data.getSearchKey().indexOf("_") > -1 || data.getSearchKey().indexOf("-") > -1) {
			spe = true;
			title = title.replace(data.getSearchKey(), "{temp}");
		}
		//其他情况
		if(title.indexOf("_") > -1) {
			title = title.split("_")[0].trim();
		}
//		if(title.indexOf("-") > -1) {
//			title = title.split("-")[0].trim();
//		}
		if(spe)
			title = title.replace("{temp}", data.getSearchKey());
		data.setTitle(title);
		data.setContent(result.getContent());
		data.setImgUrl(result.getImgs());
		data.setInserttime(new Timestamp(System.currentTimeMillis()));
		return null;
	}

	@Override
	public void processPage(BlogData data, Node domtree,
			Map<String, Component> map, String... args) {
	}
	
	@Override
	public void processList(List<BlogData> list, Node domtree,
			Map<String, Component> comp, String... args) {
		this.parseTitle(list, domtree, comp.get("title"));
		
		if (list.size() == 0) return;
		
		this.parseUrl(list, domtree, comp.get("url"));
		
		this.parseBrief(list, domtree, comp.get("brief"));
		this.parseSource(list, domtree, comp.get("source"));
		this.parsePubtime(list, domtree, comp.get("pubtime"));
		this.parseAuthor(list, domtree, comp.get("author"));
	}
	
}
	