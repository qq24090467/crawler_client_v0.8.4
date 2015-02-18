package common.extractor.xpath.bbs.search.sub;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.BBSData;
import common.extractor.xpath.bbs.search.BbsSearchXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;

/**
 * 天涯内容特殊解析
 * @author grs
 *
 */
public class TianyaExtractor extends BbsSearchXpathExtractor {
	/**
	 * 格式化发布者
	 * @param xpath
	 * @param domtree
	 * @param siteFlag
	 * @return
	 */
	@Override
	public void parseAuthor(BBSData vd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String author = "";
		if(nl.item(0)!=null)  {
			author = nl.item(0).getTextContent().replace("楼主：", "").replace("作者：", "");
		}
		vd.setAuthor(author.trim());
	}
	
	@Override
	public String parseNext(Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String url = null;
		if(nl != null && nl.item(0)!=null) {
			if(args[0].contains("&pn=")) {
				url = args[0].split("pn=")[0]+"pn="+(Integer.parseInt(args[0].split("pn=")[1])+1);
			} else {
				url = args[0]+"&pn="+(Integer.parseInt(args[1])+1);
			}
		}
		return url;
	}
	
	@Override
	public void parsePubtime(BBSData vd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl!=null && nl.item(0)!=null) {
			String time = StringUtil.format(nl.item(0).getTextContent().replace("时间：", ""));
			vd.setPubtime(time);
		}
	}
	@Override
	public void parseSource(BBSData vd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl!=null && nl.item(0)!=null) {
			String c = StringUtil.format(nl.item(0).getTextContent().replace("来自：", ""));
			vd.setPubfrom(c);
		}
		
	}
	@Override
	public void parseClickCount(BBSData vd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl!=null && nl.item(0)!=null) {
			String time = StringUtil.extrator(nl.item(0).getTextContent(), "\\d");
			if(time==null || time.equals(""))
				vd.setClickCount(0);
			else
				vd.setClickCount(Integer.parseInt(time));
		}
		
	}
	@Override
	public void parseReplyCount(BBSData vd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl!=null && nl.item(0)!=null) {
			String time = StringUtil.extrator(nl.item(0).getTextContent(), "\\d");
			if(time==null || time.equals(""))
				vd.setReplyCount(0);
			else
				vd.setReplyCount(Integer.parseInt(time));
		}
		
	}
	
}
