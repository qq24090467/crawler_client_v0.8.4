package common.extractor.xpath.blog.monitor;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.BlogData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface BlogMonitorExtractorAttribute extends ExtractorAttribute<BlogData> {

	/**
	 * 摘要解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseBrief(List<BlogData> list, Node dom, Component component, String... args);

	/**
	 * 发布源解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSource(List<BlogData> list, Node dom, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(List<BlogData> list, Node dom, Component component, String... args);

	/**
	 * 解析博文作者
	 * @param list
	 * @param domtree
	 * @param component
	 * @param content
	 */
	public void parseAuthor(List<BlogData> list, Node domtree, Component component, String... args);
}
