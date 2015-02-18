package common.extractor.xpath.weibo.monitor;

import java.util.List;

import org.w3c.dom.Node;

import common.bean.UserData;
import common.bean.WeiboData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface WeiboMonitorExtractorAttribute extends ExtractorAttribute<WeiboData> {

	/**
	 * 微博内容解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void  parseWeiboContent(List<WeiboData> list, Node dom, Component component, String... args);
	/**
	 * 微博内容解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboRttContent(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 微博链接
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboContentUrl(List<WeiboData> list, Node dom,
			Component component, String... args);
	/**
	 * 微博图片链接解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboImgUrl(List<WeiboData> list, Node dom, Component component, String... args);

	/**
	 * 微博评论数解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboCommentNum(List<WeiboData> list, Node dom, Component component, String... args);
	/**
	 * 微博转发数解析
	 * @param list
	 * @param domUtil
	 * @param component
	 * @param args
	 */
	public void parseWeiboRttNum(List<WeiboData> list, Node domtree, Component component, String... args);
	/**
	 * 发布时间解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboPubtime(List<WeiboData> list, Node dom, Component component, String... args);
	/**
	 * 发布源解析
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboSource(List<WeiboData> list, Node dom, Component component, String... args);
	/**
	 * 评论链接
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboCommentUrl(List<WeiboData> list, Node dom,	Component component, String... args);
	/**
	 * 转发链接
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboRttUrl(List<WeiboData> list, Node dom,	Component component, String... args);
	/**
	 * 微博ID抽取
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboMid(List<WeiboData> list, Node dom,	Component component, String... args);
	
	
	/**
	 * 解析博主的粉丝关注博主名
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseRelationAuthor(List<UserData> list, Node dom, Component component,
			String... args);
	/**
	 * 解析博主的粉丝关注博主URL
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationAuthorUrl(List<UserData> list, Node domtree, Component component, String... args);
	/**
	 * 解析博主的粉丝关注博主头像
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationAuthorImg(List<UserData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析博主的粉丝关注博主地址
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationAddress(List<UserData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析的粉丝关注博主的关注数
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationAttentNum(List<UserData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析的粉丝关注博主的粉丝数
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationFansNum(List<UserData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析的粉丝关注博主的微博数
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationWeiboNum(List<UserData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析的粉丝关注博主的关注链接
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationFollowUrl(List<UserData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析的粉丝关注博主的粉丝链接
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationFansUrl(List<UserData> list, Node domtree,
			Component component, String... args);
	
	/**
	 * 解析的粉丝关注博主的微博链接
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationWeiboUrl(List<UserData> list, Node domtree,
			Component component, String... args);
	/**
	 * 解析的粉丝关注博主的性别
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationSex(List<UserData> list, Node domtree,
			Component component, String... args);
	
	/**
	 * 解析的粉丝关注博主的认证
	 * @param list
	 * @param domtree
	 * @param component
	 */
	public void parseRelationCertify(List<UserData> list, Node domtree,
			Component component, String... args);
	/**
	 * 博主关系采集翻页
	 * @param dom
	 * @param component
	 * @param args
	 * @return
	 */
	public String parseRelationNext(Node dom, Component component, String... args);
	
	
	
	/**
	 * 转发博主名称抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseRttAuthor(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 转发博主URL抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseRttAuthorUrl(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 转发博主头像抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseRttAuthorImg(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 转发时间抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseRttTime(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 转发内容抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseRttContent(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 转发的链接Url抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseRttUrl(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 转发博主的ID抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseRttUid(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 转发下一页抽取
	 * @param domtree
	 * @param component
	 * @param args
	 * @return
	 */
	public String parseRttNext(Node domtree, Component component,
			String... args);
	
	
	
	/**
	 * 评论博主名称抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseCommentAuthor(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 评论博主URL抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseCommentAuthorUrl(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 评论博主头像抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseCommentAuthorImg(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 评论时间抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseCommentTime(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 评论内容抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseCommentContent(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 评论博主的ID抽取
	 * @param list
	 * @param domtree
	 * @param component
	 * @param args
	 */
	public void parseCommentUid(List<WeiboData> list, Node domtree,
			Component component, String... args);
	/**
	 * 评论下一页抽取
	 * @param domtree
	 * @param component
	 * @param args
	 * @return
	 */
	public String parseCommentNext(Node domtree, Component component,
			String... args);
	
	
	
	/**
	 * 联系方式解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	void parseConcact(UserData data, Node dom, Component component,
			String... args);
	/**
	 * 公司解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	void parseCommpany(UserData data, Node dom, Component component,
			String... args);
	/**
	 * 注册时间解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseRegistTime(UserData data, Node dom,
			Component component, String... args);
	/**
	 * 生日解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseBirth(UserData data, Node dom, Component component, String... args);
	/**
	 * 别名解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseNick(UserData data, Node dom, Component component, String... args);
	/**
	 * 介绍链接解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseInfoUrl(UserData data, Node dom, Component component, String... args);
	/**
	 * 微博链接解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboUrl(UserData data, Node dom, Component component, String... args);
	
	/**
	 * 粉丝链接解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseFansUrl(UserData data, Node dom, Component component, String... args);
	/**
	 * 关注链接解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseFollowUrl(UserData data, Node dom, Component component, String... args);
	/**
	 * 博主ID解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAuthorId(UserData data, Node dom, Component component, String... args);
	
	/**
	 * 标签解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseTag(UserData data, Node dom, Component component, String... args);
	/**
	 * 介绍解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseInfo(UserData data, Node dom, Component component, String... args);
	/**
	 * 地址解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAddress(UserData data, Node dom, Component component, String... args);
	/**
	 * 认证解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseCertify(UserData data, Node dom, Component component, String... args);
	/**
	 * 性别解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseSex(UserData data, Node dom, Component component, String... args);
	/**
	 * 微博数量解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseWeiboNum(UserData data, Node dom, Component component, String... args);
	/**
	 * 关注数量解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAttentNum(UserData data, Node dom, Component component, String... args);
	
	/**
	 * 粉丝数量解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseFansNum(UserData data, Node dom, Component component, String... args);
	/**
	 * 头像解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAuthorImg(UserData data, Node dom, Component component, String... args);
	
	/**
	 * 博主链接解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAuthorUrl(UserData data, Node dom, Component component, String... args);
	/**
	 * 博主名解析
	 * @param data
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseAuthor(UserData data, Node dom, Component component, String... args);
	
}
