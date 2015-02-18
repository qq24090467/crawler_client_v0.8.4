package common.extractor;

import java.util.List;

import org.w3c.dom.Node;

import common.siteinfo.Component;

public interface ExtractorAttribute<T> {

	/**
	 * 解析URL属性
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseUrl(List<T> list, Node dom, Component component, String... args);
	/**
	 * 解析标题属性
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseTitle(List<T> list, Node dom, Component component, String... args);

	/**
	 * 解析下一页
	 * @param dom
	 * @param component
	 * @param args
	 * @return
	 */
	public String parseNext(Node dom, Component component, String... args);
	
}
