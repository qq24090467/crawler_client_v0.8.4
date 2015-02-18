package common.extractor.xpath;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.CommonData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface SimpleExtractorAttribute extends ExtractorAttribute<CommonData> {

	/**
	 * 内容解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseContent(CommonData cd, Node dom, Component component, String... args);
	/**
	 * 发布时间列表解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(List<CommonData> list, Node dom, Component component, String... args);
	
	/**
	 * 内容页发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(CommonData cd, Node dom, Component component, String... args);
}
