package common.extractor.xpath.weixin.monitor;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.WeixinData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface WeixinMonitorExtractorAttribute extends ExtractorAttribute<WeixinData> {

	/**
	 * 摘要解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseBrief(List<WeixinData> list, Node dom, Component component, String... args);

	/**
	 * 发布源解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSource(List<WeixinData> list, Node dom, Component component, String... args);

	void parseImgUrl(WeixinData data, Node dom, Component component, String...args);

	void parseAuthor(WeixinData data, Node dom, Component component, String...args);

	void parsePubtime(WeixinData data, Node dom, Component component,
			String... args);

	void parseSource(WeixinData data, Node dom, Component component,
			String... strings);

	void parseContent(WeixinData data, Node dom, Component component,
			String... strings);
}
