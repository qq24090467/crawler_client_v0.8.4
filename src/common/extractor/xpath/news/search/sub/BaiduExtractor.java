package common.extractor.xpath.news.search.sub;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.NewsData;
import common.extractor.xpath.news.search.NewsSearchXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;
/**
 * 百度新闻搜索特殊属性抽取
 * @author grs
 *
 */
public class BaiduExtractor extends NewsSearchXpathExtractor {

	@Override
	public void parseBrief(List<NewsData> list, Node dom, Component component,
			String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		String url = "";
		for(int i = 0;i < nl.getLength();i++) {
			url = nl.item(i).getTextContent().replace("百度快照", "").replace("条相同新闻 -", "");
			list.get(i).setBrief(url);
		}
	}
	
	@Override
	public void parseSameurl(List<NewsData> list, Node dom,
			Component component, String... args) {
		if(component == null) return;
		for(int i = 0;i < list.size();i++) {
			NodeList nl = commonList("//LI["+(i+1)+component.getXpath(), dom);
			if(nl.item(0)!=null) {
				list.get(i).setSameUrl(urlProcess(component, nl.item(0)));
			}
		}
	}
	@Override
	public void parsePubtime(List<NewsData> list, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl==null) return;
		String url = "";
		for(int i = 0;i < nl.getLength();i++) {
			url = nl.item(i).getTextContent();
			url = StringUtil.extrator(url, "\\d{4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}:\\d{1,2}");
			list.get(i).setPubtime(url);
			list.get(i).setPubdate(timeProcess(url));
			list.get(i).setSource(StringUtil.format(nl.item(i).getTextContent().split("  ")[0]));
		}
	}
	@Override
	public void parseSource(List<NewsData> list, Node dom, Component component, String... strings) {
//		if(component==null) return;
//		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
//		if(nl==null) return;
//		String str = "";
//		for(int i = 0;i < nl.getLength();i++) {
//			str = nl.item(i).getTextContent();
//			if(str.indexOf("2")>0){
//            	str = str.substring(0, str.indexOf("2"));   	
//        	}
//			list.get(i).setSource(StringUtil.format(str.replace(" ", "")));
//		}
	}
	
	@Override
	public void parseSamenum(List<NewsData> list, Node dom,
		Component component, String... args) {
		if(component == null) return;
		for(int i = 0;i < list.size();i++) {
			NodeList nl = commonList("//LI["+(i+1)+component.getXpath(), dom);
			if(nl.item(0)!=null) {
				String s = StringUtil.extrator(nl.item(0).getTextContent(), "\\d+");
				list.get(i).setSamenum(Integer.parseInt(s==null?"0":s));
			}
		}
	}
	
}
