package common.extractor.xpath.weibo.monitor;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.HtmlInfo;
import common.bean.UserData;
import common.bean.WeiboData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * 微博抽取实现类
 * @author grs
 */
public class WeiboMonitorXpathExtractor extends XpathExtractor<WeiboData> implements WeiboMonitorExtractorAttribute {
	@Override
	public String templateContentPage(WeiboData data, HtmlInfo html, int page,
			String... keyword) {
		return null;
	}
	/**
	 * 解析博主信息
	 * @param html
	 * @param siteFlag
	 * @param collectFlag
	 */
	public void templateUser(UserData data, HtmlInfo html, boolean first, String... keyword) {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if(domtree == null ){
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return;
		}
		
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));//得到数据的配置组件
		if(first) {
			parseAuthor(data, domtree, comp.getComponents().get("author"));
			parseAuthorImg(data, domtree, comp.getComponents().get("author_img"));
			parseAuthorUrl(data, domtree, comp.getComponents().get("author_url"));
			parseAuthorId(data, domtree, comp.getComponents().get("author_id"));
			parseFansNum(data, domtree, comp.getComponents().get("fans_num"));
			parseAttentNum(data, domtree, comp.getComponents().get("attent_num"));
			parseWeiboNum(data, domtree, comp.getComponents().get("weibo_num"));
			parseCertify(data, domtree, comp.getComponents().get("certify"));
			parseAddress(data, domtree, comp.getComponents().get("address"));
			parseInfo(data, domtree, comp.getComponents().get("content"));
			parseTag(data, domtree, comp.getComponents().get("tag"));
			parseSex(data, domtree, comp.getComponents().get("sex"));
			parseFansUrl(data, domtree, comp.getComponents().get("fans_url"));
			parseFollowUrl(data, domtree, comp.getComponents().get("follow_url"));
			parseWeiboUrl(data, domtree, comp.getComponents().get("weibo_url"));
			parseInfoUrl(data, domtree, comp.getComponents().get("info_url"));
			data.setMd5(MD5Util.MD5(data.getAuthorUrl()));
			data.setSiteId(siteinfo.getSiteFlag());
			data.setCategoryCode(Integer.parseInt(keyword[0]));
		} else {
			parseNick(data, domtree, comp.getComponents().get("nick"));
			parseBirth(data, domtree, comp.getComponents().get("birth"));
			parseRegistTime(data, domtree, comp.getComponents().get("regist_time"));
			parseCommpany(data, domtree, comp.getComponents().get("company"));
			parseConcact(data, domtree, comp.getComponents().get("concact"));
			data.setInserttime(new Date());
		}
	}
	/**
	 * 抽取博主发布的微博数据
	 * @param list
	 * @param html
	 * @param page
	 * @param keyword
	 * @return
	 */
	public String templateListPage(List<WeiboData> list, HtmlInfo html, int page, String... keyword) {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if(domtree == null ){
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));//得到数据的配置组件
		parseWeiboMid(list, domtree, comp.getComponents().get("weibo_mid"));
		if(list.size()==0) return null;
		parseWeiboContent(list, domtree, comp.getComponents().get("weibo_content"));
		parseWeiboContentUrl(list, domtree, comp.getComponents().get("weibo_content_url"));
		parseWeiboImgUrl(list, domtree, comp.getComponents().get("weibo_img_url"));
		parseWeiboCommentNum(list, domtree, comp.getComponents().get("weibo_comment_num"));
		parseWeiboRttNum(list, domtree, comp.getComponents().get("weibo_rtt_num"));
		parseWeiboPubtime(list, domtree, comp.getComponents().get("weibo_pubtime"));
		parseWeiboRttContent(list, domtree, comp.getComponents().get("weibo_rtt_content"));
		parseWeiboSource(list, domtree, comp.getComponents().get("weibo_source"));
		parseWeiboCommentUrl(list, domtree, null);
		parseWeiboRttUrl(list, domtree, null);
		
		weiboAttributeSet(list, siteinfo.getSiteFlag(), keyword);
		String nextPage = parseNext(domtree, comp.getComponents().get("weibo_next"), 
				new String[]{keyword[1],list.get(list.size()-1).getMid(), StringUtil.regMatcher(html.getContent(), "<AUTHORID>", "</AUTHORID>"), page+"", list.get(0).getMid()});
		domtree = null;
		return nextPage;
	}
	
	protected void weiboAttributeSet(List<WeiboData> list, int siteId, String... keyword) {
		for(WeiboData data : list) {
			data.setMd5(MD5Util.MD5(data.getMid()));
			//博主ID
			data.setDataId(Integer.parseInt(keyword[0]));
			data.setSiteId(siteId);
			data.setCategoryCode(Integer.parseInt(keyword[2]));
			data.setInserttime(new Date());
		}
	}
	/**
	 * 转发数据抽取
	 * @param list
	 * @param html
	 * @param page
	 * @param keyword
	 * @return
	 */
	public String templateRtt(List<WeiboData> list, HtmlInfo html, int page, String... keyword) {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if(domtree == null ){
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));//得到数据的配置组件
		parseRttAuthor(list, domtree, comp.getComponents().get("rtt_author"));
		parseRttAuthorImg(list, domtree, comp.getComponents().get("rtt_author_img"));
		parseRttAuthorUrl(list, domtree, comp.getComponents().get("rtt_author_url"));
		parseRttTime(list, domtree, comp.getComponents().get("rtt_time"));
		parseRttContent(list, domtree, comp.getComponents().get("rtt_content"));
		parseRttUrl(list, domtree, comp.getComponents().get("rtt_url"));
		parseRttUid(list, domtree, comp.getComponents().get("rtt_uid"));
		
		for(WeiboData wd : list) {
			wd.setMd5(MD5Util.MD5(wd.getUrl()));
			wd.setId(Integer.parseInt(keyword[0]));
			wd.setInserttime(new Date());
		}

		String nextPage = parseRttNext(domtree, comp.getComponents().get("rtt_next"));
		domtree = null;
		return nextPage;
	}
	/**
	 * 评论数据抽取
	 * @param list
	 * @param html
	 * @param page
	 * @param keyword
	 * @return
	 */
	public String templateComment(List<WeiboData> list, HtmlInfo html,
			int page, String... keyword) {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if(domtree == null ){
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));//得到数据的配置组件
		parseCommentAuthor(list, domtree, comp.getComponents().get("comment_author"));
		parseCommentAuthorUrl(list, domtree, comp.getComponents().get("comment_author_url"));
		parseCommentAuthorImg(list, domtree, comp.getComponents().get("comment_author_img"));
		parseCommentTime(list, domtree, comp.getComponents().get("comment_time"));
		parseCommentContent(list, domtree, comp.getComponents().get("comment_content"));
		parseCommentUid(list, domtree, comp.getComponents().get("comment_uid"));
		for(WeiboData wd : list) {
			wd.setMd5(MD5Util.MD5(wd.getUid()+wd.getPubtime()));
			wd.setId(Integer.parseInt(keyword[0]));
			wd.setInserttime(new Date());
		}
		String nextPage = parseCommentNext(domtree, comp.getComponents().get("comment_next"));
		domtree = null;
		return nextPage;
	}
	/**
	 * 解析粉丝和关注
	 * @param list
	 * @param html
	 * @param page
	 * @param siteFlag
	 * @param collectFlag
	 * @param keyword
	 * @return
	 */
	public String templateRelation(List<UserData> list, HtmlInfo html, int page, String... keyword) {
		list.clear();
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if(domtree == null ){
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		String type = html.getType().substring(0, html.getType().indexOf(File.separator));
		CommonComponent comp = getRealComp(siteinfo, type);//得到元数据的配置组件
		parseRelationAuthor(list, domtree, comp.getComponents().get("fans_author"), html.getContent());
		if (list.size() == 0) return null;
		
		parseRelationAuthorUrl(list, domtree, comp.getComponents().get("fans_author_url"), html.getContent());
		parseRelationAuthorImg(list, domtree, comp.getComponents().get("fans_author_img"), html.getContent());
		parseRelationAddress(list, domtree, comp.getComponents().get("fans_address"), html.getContent());
		parseRelationAttentNum(list, domtree, comp.getComponents().get("fans_attent_num"), html.getContent());
		parseRelationFansNum(list, domtree, comp.getComponents().get("fans_fans_num"), html.getContent());
		parseRelationWeiboNum(list, domtree, comp.getComponents().get("fans_weibo_num"), html.getContent());
		parseRelationFollowUrl(list, domtree, comp.getComponents().get("fans_follow_url"), html.getContent());
		parseRelationFansUrl(list, domtree, comp.getComponents().get("fans_fans_url"), html.getContent());
		parseRelationWeiboUrl(list, domtree, comp.getComponents().get("fans_weibo_url"), html.getContent());
		parseRelationSex(list, domtree, comp.getComponents().get("fans_sex"), html.getContent());
		parseRelationCertify(list, domtree, comp.getComponents().get("fans_certify"), html.getContent());
		for(UserData vd : list) {
			vd.setMd5(MD5Util.MD5(vd.getAuthorUrl()));
			vd.setInserttime(new Date());
			vd.setPersonId(Integer.parseInt(keyword[0]));
		}
		
		String nextPage = parseRelationNext(domtree, comp.getComponents().get("fans_next"), new String[]{keyword[1], page+""});
		domtree = null;
		return nextPage;
	}
	
	/*以下为微博数据抽取方法*/
	@Override
	public void parseWeiboCommentUrl(List<WeiboData> list, Node dom,
			Component component, String... args) {
	}
	@Override
	public void parseWeiboRttUrl(List<WeiboData> list, Node dom,
			Component component, String... args) {
	}
	@Override
	public String parseNext(Node dom, Component component, String... strings) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}
	@Override
	public void parseWeiboMid(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			WeiboData data = new WeiboData();
			data.setMid(nl.item(i).getTextContent());
			list.add(data);
		}
	}
	@Override
	public void parseWeiboPubtime(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent());
			list.get(i).setPubdate(timeProcess(nl.item(i).getTextContent()));
		}
	}
	@Override
	public void parseWeiboRttNum(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String s = StringUtil.extrator(nl.item(i).getTextContent(), "\\d+");
			if(s.equals("")) 
				list.get(i).setRttNum(0);
			else
				list.get(i).setRttNum(Integer.parseInt(s));
		}
	}
	@Override
	public void parseWeiboContentUrl(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i)));
		}
	}
	@Override
	public void parseWeiboCommentNum(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			String s = StringUtil.extrator(nl.item(i).getTextContent(), "\\d+");
			if(s.equals("")) 
				list.get(i).setCommentNum(0);
			else
				list.get(i).setCommentNum(Integer.parseInt(s));
		}
	}
	@Override
	public void parseWeiboImgUrl(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setImgUrl(urlProcess(component, nl.item(i)));
		}
	}
	@Override
	public void parseWeiboContent(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setContent(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseWeiboRttContent(List<WeiboData> list, Node domtree,
			Component component, String... args) {
	}
	
	/*以下为转发数据抽取方法*/
	@Override
	public String parseRttNext(Node domtree, Component component,
			String... args) {
		NodeList nl = head(component.getXpath(), domtree);
		if(nl.item(0)!=null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}
	@Override
	public void parseRttUid(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setUid(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseRttUrl(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setUrl(urlProcess(component, nl.item(i)));
		}
	}
	@Override
	public void parseRttContent(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setBrief(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseRttTime(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseRttAuthorImg(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setAuthorImg(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseRttAuthorUrl(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setAuthorurl(urlProcess(component, nl.item(i)));
		}
	}
	@Override
	public void parseRttAuthor(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), domtree);
		for(int i = 0;i < nl.getLength();i++) {
			WeiboData data = new WeiboData();
			data.setAuthor(nl.item(i).getTextContent());
			list.add(data);
		}
	}
	
	/*以下为评论数据抽取方法*/
	@Override
	public String parseCommentNext(Node domtree, Component component,
			String... args) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl.item(0)!=null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}
	@Override
	public void parseCommentContent(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setBrief(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseCommentTime(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseCommentAuthorImg(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setAuthorImg(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseCommentAuthorUrl(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setAuthorurl(urlProcess(component, nl.item(i)));
		}
	}
	@Override
	public void parseCommentAuthor(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree);
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			WeiboData data = new WeiboData();
			data.setAuthor(nl.item(i).getTextContent());
			list.add(data);
		}
	}
	@Override
	public void parseCommentUid(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setUid(nl.item(i).getTextContent());
		}
	}
	
	/*以下为博主信息抽取方法*/
	@Override
	public void parseConcact(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		
		String brief = "";
		for(int i = 0;i < nl.getLength();i++) {
			brief += nl.item(i).getTextContent();
		}
		data.setConcact(StringUtil.format(brief));
	}
	@Override
	public void parseCommpany(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		
		if(nl.item(0)!=null) 
			data.setCompany(nl.item(0).getTextContent());
	}
	@Override
	public void parseRegistTime(UserData data, Node dom,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		
		if(nl.item(0)!=null) 
			data.setRegistTime(nl.item(0).getTextContent());
	}
	@Override
	public void parseBirth(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		
		if(nl.item(0)!=null) 
			data.setBirth(nl.item(0).getTextContent());
	}
	@Override
	public void parseNick(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		
		if(nl.item(0)!=null) 
			data.setNick(nl.item(0).getTextContent());
	}
	@Override
	public void parseInfoUrl(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) {
			data.setInfoUrl(urlProcess(component, nl.item(0)));
		}
	}
	@Override
	public void parseWeiboUrl(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) {
			data.setWeiboUrl(urlProcess(component, nl.item(0)));
		}
	}
	@Override
	public void parseFollowUrl(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) {
			data.setFollowUrl(urlProcess(component, nl.item(0)));
		}
	}
	@Override
	public void parseFansUrl(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) {
			data.setFansUrl(urlProcess(component, nl.item(0)));
		}
	}
	@Override
	public void parseAuthorId(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) 
			data.setAuthorId(nl.item(0).getTextContent());
	}
	@Override
	public void parseTag(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		String brief = "";
		for(int i = 0;i < nl.getLength();i++) {
			brief += nl.item(i).getTextContent();
		}
		data.setTag(brief);
	}
	@Override
	public void parseInfo(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		String brief = "";
		for(int i = 0;i < nl.getLength();i++) {
			brief += nl.item(i).getTextContent();
		}
		data.setContent(brief);
	}
	@Override
	public void parseAddress(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) 
			data.setAddress(nl.item(0).getTextContent());
	}
	@Override
	public void parseCertify(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) 
			data.setCertify(nl.item(0).getTextContent());
	}
	@Override
	public void parseSex(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) 
			data.setSex(nl.item(0).getTextContent());
	}
	@Override
	public void parseWeiboNum(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) 
			data.setWeiboNum(Integer.parseInt(nl.item(0).getTextContent()));
	}
	@Override
	public void parseAttentNum(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) 
			data.setAttentNum(Integer.parseInt(nl.item(0).getTextContent()));
	}
	@Override
	public void parseFansNum(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) 
			data.setFansNum(Integer.parseInt(nl.item(0).getTextContent()));
	}
	@Override
	public void parseAuthorImg(UserData data, Node dom, Component component, String... strings) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		
		if(nl.item(0)!=null) 
			data.setAuthorImg(nl.item(0).getTextContent());
	}
	@Override
	public void parseAuthorUrl(UserData data, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) 
			data.setAuthorUrl(nl.item(0).getTextContent());
	}
	@Override
	public void parseAuthor(UserData data, Node dom, Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) 
			data.setAuthor(nl.item(0).getTextContent());
	}
	
	/*以下为博主的关系数据抽取方法*/
	@Override
	public void parseRelationCertify(List<UserData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setCertify(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseRelationSex(List<UserData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setSex(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseRelationWeiboUrl(List<UserData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setWeiboUrl(urlProcess(component, nl.item(i)));
		}
	}
	@Override
	public void parseRelationFansUrl(List<UserData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setFansUrl(urlProcess(component, nl.item(i)));
		}
	}

	@Override
	public void parseRelationFollowUrl(List<UserData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setFollowUrl(urlProcess(component, nl.item(i)));
		}
	}
	@Override
	public void parseRelationWeiboNum(List<UserData> list, Node domtree,
			Component component, String... content) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setWeiboNum(Integer.parseInt(nl.item(i).getTextContent()));
		}
	}
	@Override
	public void parseRelationFansNum(List<UserData> list, Node domtree,
			Component component, String... content) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setFansNum(Integer.parseInt(nl.item(i).getTextContent()));
		}
	}
	@Override
	public void parseRelationAttentNum(List<UserData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setAttentNum(Integer.parseInt(nl.item(i).getTextContent()));
		}
	}
	@Override
	public void parseRelationAddress(List<UserData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setAddress(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseRelationAuthorImg(List<UserData> list, Node domtree,
			Component component, String... content) {
		if(component==null) return;
		NodeList nl =head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setAuthorImg(nl.item(i).getTextContent());
		}
	}
	@Override
	public void parseRelationAuthorUrl(List<UserData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setAuthorUrl(urlProcess(component, nl.item(i)));
		}
	}
	@Override
	public void parseRelationAuthor(List<UserData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			UserData data = new UserData();
			data.setAuthor(nl.item(i).getTextContent());
			list.add(data);
		}
	}
	@Override
	public String parseRelationNext(Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl.item(0)!=null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}
	
	
	@Override
	public void parseUrl(List<WeiboData> list, Node dom, Component component,
			String... args) {
	}
	@Override
	public void parseTitle(List<WeiboData> list, Node dom, Component component,
			String... args) {
	}
	@Override
	public void parseWeiboSource(List<WeiboData> list, Node dom,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		if(nl == null) return;
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setSource(nl.item(i).getTextContent());
		}
	}
	@Override
	public void processPage(WeiboData data, Node domtree,
			Map<String, Component> map, String... args) {
	}
	@Override
	public void processList(List<WeiboData> list, Node domtree,
			Map<String, Component> components, String... args) {
	}

}
	