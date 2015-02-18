package common.extractor.xpath.weibo.search;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.WeiboData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface WeiboSearchExtractorAttribute extends ExtractorAttribute<WeiboData> {

	/**
	 * 评论链接
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseCommenturl(List<WeiboData> list, Node dom,
			Component component, String... args);
	/**
	 * 转发链接
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseRtturl(List<WeiboData> list, Node dom,
			Component component, String... args);
	/**
	 * 微博内容解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseContent(List<WeiboData> list, Node dom, Component component, String... args);

	/**
	 * 发布源解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSource(List<WeiboData> list, Node dom, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePubtime(List<WeiboData> list, Node dom, Component component, String... args);

	/**
	 * 作者ID
	 * @param list
	 * @param domUtil
	 * @param component
	 * @param args
	 */
	public void parseUid(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析图片链接
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseImgUrl(List<WeiboData> list, Node dom, Component component,
			String... args);
	/**
	 * 解析微博的MID
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseMid(List<WeiboData> list, Node domtree, Component component);
	/**
	 * 解析微博的转发内容
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRttContent(List<WeiboData> list, Node domtree,
			Component component, String... content);
	/**
	 * 解析微博的评论次数
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseCommentNum(List<WeiboData> list, Node domtree,
			Component component);
	/**
	 * 解析微博的转发次数
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRttNum(List<WeiboData> list, Node domtree,
			Component component);
	/**
	 * 解析微博的作者URL
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseAuthorUrl(List<WeiboData> list, Node domtree,
			Component component, String... content);
	/**
	 * 解析微博的作者头像
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseAuthorImg(List<WeiboData> list, Node domtree,
			Component component, String... content);
	/**
	 * 解析微博的作者名
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseAuthor(List<WeiboData> list, Node domtree,
			Component component);
}
