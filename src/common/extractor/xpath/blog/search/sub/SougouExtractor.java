package common.extractor.xpath.blog.search.sub;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.BlogData;
import common.extractor.xpath.blog.search.BlogSearchXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;


public class SougouExtractor extends BlogSearchXpathExtractor {

	@Override
	public void parseAuthor(List<BlogData> list, Node dom,
			Component component, String... content) {
		if(component == null) return;
		for(int i = 0;i < list.size();i++) {
			NodeList nl = commonList("//DIV["+(i+1)+component.getXpath(), dom);
			if(nl.item(0)!=null) {
				String[] con = nl.item(0).getTextContent().split("　");
				if(con.length>2) {
					list.get(i).setBlogName(con[0].replace("博客名称：", ""));
					list.get(i).setBlogAuthor(con[1].replace("作者：", ""));
					if(list.get(i).getPubtime()==null || list.get(i).getPubtime().equals(""))
						list.get(i).setPubtime(con[2]);
				}
			}
		}
	}
	@Override
	public void parseSource(List<BlogData> list, Node dom,
			Component component, String... args) {
		if(component == null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String arr[] = nl.item(i).getTextContent().split("- ");
			list.get(i).setSource(arr[0]);
			if(arr.length>2) {
				list.get(i).setPubtime(StringUtil.extrator(arr[2], "[12]\\d{1,3}-\\d{1,2}-\\d{1,2}"));
			} else {
				list.get(i).setPubtime(StringUtil.extrator(nl.item(i).getTextContent(), "[12]\\d{1,3}-\\d{1,2}-\\d{1,2}"));
			}
			list.get(i).setPubdate(timeProcess(list.get(i).getPubtime()));
		}
	}
	
	@Override
	public void parseBrief(List<BlogData> list, Node dom, Component component,
			String... args) {
		if(component == null) return;
		for(int i = 0;i < list.size();i++) {
			NodeList nl = commonList("//DIV[@class='rb']["+(i+1)+component.getXpath(), dom);
			if(nl.item(0)!=null) {
				list.get(i).setBrief(nl.item(0).getTextContent());
			}
		}
	}
}
