package common.extractor.xpath.weibo.monitor.sub;


import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.HtmlInfo;
import common.bean.UserData;
import common.bean.WeiboData;
import common.extractor.xpath.weibo.monitor.WeiboMonitorXpathExtractor;
import common.siteinfo.Component;
import common.util.JsonUtil;
import common.util.StringUtil;
/**
 * 
 * @author grs
 *
 */
public class SinaExtractor extends WeiboMonitorXpathExtractor {

	@Override
	protected Node getRealDOM(HtmlInfo html) {
		String content = html.getContent();
		String con = "";
		String flag = html.getType().substring(0, html.getType().indexOf(File.separator));
		if (html.getOrignUrl().contains("http://weibo.com/p/aj/mblog/mbloglist")) {
			try {
				con = JsonUtil.getStringByKey(content, "data");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(html.getOrignUrl().startsWith("http://weibo.com/aj")) {
			try {
				con = JsonUtil.getJsonObjectByKey(content, "data").getString("html");
			} catch (Exception e) {
//				e.printStackTrace();
			}
		} else {
			String id = StringUtil.regMatcher(content, "\\$CONFIG\\['page_id'\\]\\s*=\\s*'", "'");
			if(flag.equals("USER")) {
				String temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.header.head.index\",", "\\)</script>");
				if(temp!=null) {
					con = JsonUtil.getStringByKey("{"+temp, "html");
				}
				temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.nav.index\",", "\\)</script>");
				if(temp!=null) {
					con += JsonUtil.getStringByKey("{"+temp, "html");
				}
			} else if(flag.equals("USERINFO")) {
				String temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.content.textMulti.index\",", "\\)</script>");
				if(temp!=null) {
					con = JsonUtil.getStringByKey("{"+temp, "html");
				}
				temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"\",\"domid\":\"Pl_Official_LeftInfo__25\",", "\\)</script>");
				if(temp!=null) {
					con += JsonUtil.getStringByKey("{"+temp, "html");
				}
			} else if(flag.equals("FANS")) {
				String temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.content.followTab.index\",", "\\)</script>");
				if(temp!=null) {
					con = JsonUtil.getStringByKey("{"+temp, "html");
				}
			} else if(flag.equals("FOLLOW")) {
				String temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.content.followTab.index\",", "\\)</script>");
				if(temp!=null) {
					con = JsonUtil.getStringByKey("{"+temp, "html");
				}
			} else if(flag.equals("DATA")) {
				String temp = StringUtil.regMatcher(content, "<script>FM.view\\(\\{\"ns\":\"pl.content.homeFeed.index\",", "\\)</script>");
				if(temp!=null) {
					con = JsonUtil.getStringByKey("{"+temp, "html");
				}
			}
			if(id!=null && !id.equals(""))
				con += "<AUTHORID>"+id+"</AUTHORID>";
		}
		html.setContent(con);
		return super.getRealDOM(html);
	}
	
	@Override
	public void parseFansUrl(UserData data, Node dom, Component component, String... args) {
		super.parseFansUrl(data, dom, component);
		String url = data.getFansUrl();
		if(url!= null && url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"))+"?relate=fans";
		}
		data.setFansUrl(url);
	}
	
	@Override
	public void parseFollowUrl(UserData data, Node dom, Component component, String... args) {
		super.parseFollowUrl(data, dom, component);
		String url = data.getFollowUrl();
		if(url!= null && url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		data.setFollowUrl(url);
	}
	
	@Override
	public void parseInfoUrl(UserData data, Node dom, Component component, String... args) {
		super.parseInfoUrl(data, dom, component);
		String url = data.getInfoUrl();
		if(url!= null && url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		data.setInfoUrl(url);
	}
	
	@Override
	public void parseWeiboUrl(UserData data, Node dom, Component component, String... args) {
		super.parseWeiboUrl(data, dom, component);
		String url = data.getWeiboUrl();
		if(url!= null && url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		data.setWeiboUrl(url);
	}
	
	@Override
	public void parseRelationSex(List<UserData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		for(int i = 0;i < nl.getLength();i++) {
			if(nl.item(i).getTextContent().endsWith("female"))
				list.get(i).setSex("女");
			else
				list.get(i).setSex("男");
		}
	}
	
	@Override
	public void parseRelationCertify(List<UserData> list, Node domtree,
			Component component, String... args) {
		for(int i=0;i < list.size();i++) {
			NodeList nl = commonList("//LI["+(i+1)+component.getXpath(), domtree);
			if(nl.item(0)!=null)
				list.get(i).setCertify(nl.item(0).getTextContent());
		}
	}
	
	
	@Override
	public void parseWeiboImgUrl(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		for(int i=0;i < list.size();i++) {
			NodeList nl = commonList("//DIV[@action-type][not(@feedType)]["+(i+1)+component.getXpath(), domtree);
			String url = "";
			for(int k = 0;k < nl.getLength();k++) {
				url += nl.item(k).getTextContent()+";";
			}
			list.get(i).setImgUrl(url);
		}
	}
	
	@Override
	public void parseWeiboRttContent(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		for(int i=0;i < list.size();i++) {
			NodeList nl = commonList("//DIV[@action-type][not(@feedType)]["+(i+1)+component.getXpath(), domtree);
			String url = null;
			for(int k = 0;k < nl.getLength();k++) {
				url += nl.item(k).getTextContent();
			}
			list.get(i).setContent(list.get(i).getContent()+ url==null? "":"\r\n转发\r\n" + url);
		}
	}
	private String nextPrefix;
	@Override
	public String parseNext(Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		if(nl==null) return null;
		String url = null;
		if(nl.item(0)!=null) {
			String id = args[2];
			String page = args[3];
			String maxId = args[1];
			String lastUrl = args[0];
			
			String pagebar = "0";
			String endid = args[4];
			
			if(lastUrl.startsWith("http://weibo.com/p/aj/mblog/mbloglist")) {
				pagebar = "1";
				endid = StringUtil.regMatcher(lastUrl, "end_id=", "&");
				id = StringUtil.regMatcher(lastUrl, "&id=", "&");
			} else {
				nextPrefix = lastUrl;
				if(nextPrefix.indexOf("?")>-1) {
					nextPrefix = nextPrefix.substring(0, nextPrefix.indexOf("?"));
				}
			}
			url = nl.item(0).getTextContent();
			if(url.contains("正在加载")) {
				url = "http://weibo.com/p/aj/mblog/mbloglist?domain=100505&pre_page="+page+"&page="+page+"&max_id="+maxId+"&end_id="+endid+"&count=15&pagebar="+pagebar+"&max_msign=&filtered_min_id=&id="+id+"&feed_type=0";
			} else {
				if(component.getPrefix()!=null)
					url = component.getPrefix() + url;
				if(component.getPostfix()!=null)
					url += component.getPostfix();
				if(url.indexOf("#")>-1)
					url = url.substring(0, url.indexOf("#")).replace("http://weibo.com/p/aj/mblog/mbloglist", nextPrefix);
			}
		}
		return url;
	}
	@Override
	public void parseWeiboCommentUrl(List<WeiboData> list, Node dom,
			Component component, String... args) {
		for(int i = 0;i < list.size();i++) {
			list.get(i).setCommentUrl("http://weibo.com/aj/comment/big?id="+list.get(i).getMid());
		}
	}
	
	@Override
	public void parseWeiboRttUrl(List<WeiboData> list, Node dom,
			Component component, String... args) {
		for(int i = 0;i < list.size();i++) {
			list.get(i).setRttUrl("http://weibo.com/aj/mblog/info/big?id="+list.get(i).getMid());
		}
	}
	
	@Override
	public void parseCommentUid(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setUid(nl.item(i).getTextContent().replace("id=", ""));
		}
	}
	
	@Override
	public void parseRttUid(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		parseCommentUid(list, domtree, component);
	}
	
	@Override
	public void parseWeiboPubtime(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		timeCommon(list, domtree, component);
	}

	@Override
	public void parseCommentTime(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		timeCommon(list, domtree, component);
	}
	
	@Override
	public void parseRttTime(List<WeiboData> list, Node domtree,
			Component component, String... args) {
		timeCommon(list, domtree, component);
	}
	
	private void timeCommon(List<WeiboData> list, Node domtree,
			Component component) {
		if(component==null) return;
		NodeList nl = head(component.getXpath(), domtree, list.size(), component.getName());
		for(int i = 0;i < nl.getLength();i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent());
			list.get(i).setPubdate(timeprocess(nl.item(i).getTextContent()));
		}
	}
	
	private Date timeprocess(String s) {
		Date d = super.timeProcess(s);
		if(d == null) {
			Calendar c = Calendar.getInstance();
			if(s.indexOf("月")>-1 || s.indexOf("日")>-1) {
				if(s.indexOf("年")==-1) {
					s = c.get(Calendar.YEAR)+"-"+s;
				}
				s = s.replace("年", "-").replace("月", "-").replace("日", "");
				d = super.timeProcess(s);
			}
			if(d==null) {
				
				int num = Integer.parseInt(StringUtil.extrator(s, "\\d"));
				if(s.contains("minute")|| s.contains("分钟前")) {
					c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)-num);
				} else if(s.contains("hour") || s.contains("小时前")) {
					c.set(Calendar.HOUR, c.get(Calendar.HOUR)-num);
				} else if(s.contains("今天")) {
					s.replace("今天", c.get(Calendar.YEAR)+"-"+c.get(Calendar.MONTH)+"-"+c.get(Calendar.DATE)+"");
				} else if(s.contains("day") || s.contains("天前")) {
					c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-num);
				} else if(s.contains("month") || s.contains("月前")) {
					c.set(Calendar.MONTH, c.get(Calendar.MONTH)-num);
				} else if(s.contains("year") || s.contains("年前")) {
					c.set(Calendar.YEAR, c.get(Calendar.YEAR)-num);
				} else if(s.contains("second") || s.contains("秒前")) {
					c.set(Calendar.SECOND, c.get(Calendar.SECOND)-num);
				}
				return c.getTime();
			}
		}
		return d;
	}
	
}
