package common.extractor.xpath.bbs.monitor;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.BBSData;
import common.bean.ReplyData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface BbsMonitorExtractorAttribute extends ExtractorAttribute<BBSData> {
	/**
	 * 内容解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseContent(BBSData data, Node dom, Component component, String... args);

	/**
	 * 作者解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAuthor(BBSData data, Node dom, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(BBSData data, Node dom, Component component, String... args);
	/**
	 * 解析点击次数
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseClickCount(BBSData data, Node domtree, Component component, String... ags);

	/**
	 * 解析来源
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseSource(BBSData data, Node domtree, Component component,
			String... args);
	/**
	 * 解析回复数量
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseReplyCount(BBSData data, Node domtree,
			Component component, String... ags);
	/**
	 * 解析板块
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseColumn(BBSData data, Node domtree, Component component,
			String... ags);
	/**
	 * 解析图片链接
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseImgUrl(BBSData data, Node domtree, Component component,
			String... args);
	/**
	 * 解析标题
	 * @param data
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parsePageTitle(BBSData data, Node domtree, Component component,
			String... args);

	/**
	 * 解析回复作者
	 * @param list
	 * @param domtree
	 * @param component
	 * @param strings
	 */
	public void parseReplyname(List<ReplyData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析回复时间
	 * @param list
	 * @param domtree
	 * @param component
	 * @param strings
	 */
	public void parseReplytime(List<ReplyData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析回复内容
	 * @param list
	 * @param domtree
	 * @param component
	 * @param strings
	 */
	public void parseReplycontent(List<ReplyData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析下一页回复链接
	 * @param list
	 * @param domtree
	 * @param component
	 * @param strings
	 */
	public String parseReplyNext(Node domtree, Component component);
	
}
