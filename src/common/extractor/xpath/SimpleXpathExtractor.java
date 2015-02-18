package common.extractor.xpath;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.CommonData;
import common.siteinfo.Component;
import common.util.StringUtil;
/**
 * 简单抽取类，默认抽取实现
 * @author grs
 *
 */
public class SimpleXpathExtractor extends XpathExtractor<CommonData> implements SimpleExtractorAttribute {

	@Override
	public void processPage(CommonData data, Node domtree,
			Map<String, Component> comp, String... args) {
		this.parsePubtime(data, domtree, comp.get("pubtime"));
		this.parseContent(data, domtree, comp.get("content"));
	}

	@Override
	public void processList(List<CommonData> list, Node domtree,
			Map<String, Component> comp, String... args) {
		this.parseTitle(list, domtree, comp.get("title"));
		if (list.size() == 0)
			return;
		this.parseUrl(list, domtree, comp.get("url"));
		this.parsePubtime(list, domtree, comp.get("pubtime"));
	}
	
	@Override
	public void parseUrl(List<CommonData> list, Node dom, Component component,
			String... args) {
		if (component == null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null) return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i)));			
		}
	}

	@Override
	public void parseTitle(List<CommonData> list, Node dom,
			Component component, String... args) {
		if (component == null) return;
		NodeList nl = head(component.getXpath(), dom);
		if (nl == null) return;
		for (int i = 0; i < nl.getLength(); i++) {
			CommonData cd = new CommonData();
			cd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			list.add(cd);
		}
	}

	@Override
	public void parseContent(CommonData cd, Node dom, Component component,
			String... args) {
		if (component == null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null) return;
		String con = "";
		for (int i = 0; i < nl.getLength(); i++) {
			con += nl.item(i).getTextContent()+"\n";
		}
		cd.setContent(con);
	}

	@Override
	public void parsePubtime(List<CommonData> list, Node dom,
			Component component, String... args) {
		if (component == null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if (nl == null) return;
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setPubdate(timeProcess(nl.item(i).getTextContent()));
		}
	}

	@Override
	public void parsePubtime(CommonData cd, Node dom, Component component,
			String... args) {
		if (component == null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null) return;
		if(nl.item(0) != null)
			cd.setPubdate(timeProcess(nl.item(0).getTextContent()));
	}

}
