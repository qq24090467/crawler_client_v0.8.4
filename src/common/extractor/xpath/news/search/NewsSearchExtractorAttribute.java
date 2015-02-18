package common.extractor.xpath.news.search;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.NewsData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface NewsSearchExtractorAttribute extends ExtractorAttribute<NewsData> {

	/**
	 * 进入相同新闻的链接解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSameurl(List<NewsData> list, Node dom,
			Component component, String... args);
	/**
	 * 相同新闻数量解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSamenum(List<NewsData> list, Node dom,
			Component component, String... args);
	/**
	 * 摘要解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseBrief(List<NewsData> list, Node dom, Component component, String... args);

	/**
	 * 发布源解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSource(List<NewsData> list, Node dom, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(List<NewsData> list, Node dom, Component component, String... args);
}
