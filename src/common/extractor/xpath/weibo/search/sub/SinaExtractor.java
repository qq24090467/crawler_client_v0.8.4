package common.extractor.xpath.weibo.search.sub;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.HtmlInfo;
import common.bean.WeiboData;
import common.extractor.xpath.weibo.search.WeiboSearchXpathExtractor;
import common.siteinfo.Component;
import common.util.JsonUtil;
import common.util.StringUtil;

public class SinaExtractor extends WeiboSearchXpathExtractor {

	@Override
	protected Node getRealDOM(HtmlInfo html) {
		String content = html.getContent();
		if (content == null)
			return null;
		String temp = StringUtil.regMatcher(content,
				"<script>STK && STK.pageletM && STK.pageletM.view\\(\\{\"pid\":\"pl_wb_feedlist\",", "\\)</script>");
		if (temp != null) {
			content = "{" + temp;
			content = JsonUtil.getStringByKey(content, "html");
		}
		html.setContent(content);

		return super.getRealDOM(html);
	}

	@Override
	public void parseUid(List<WeiboData> list, Node domtree, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), domtree);
		judge(list.size(), nl.getLength());

		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setUid(StringUtil.regMatcher(nl.item(i).getTextContent(), "id=", "&"));
		}
	}

	@Override
	public void parseImgUrl(List<WeiboData> list, Node dom, Component component, String... content) {

		String xpath = component.getXpath();
		for (int i = 0; i < list.size(); i++) {
			xpath = xpath.replace("[index]", "[" + (i + 1) + "]");
			NodeList nl = commonList(xpath, dom);
			if (nl.item(0) != null)
				list.get(i).setImgUrl(nl.item(0).getTextContent());
		}
	}

	public void parseRttContent(List<WeiboData> list, Node dom, Component component, String... content) {
		for (int i = 0; i < list.size(); i++) {
			NodeList nl = commonList("//DL[" + (i + 1) + component.getXpath(), dom);
			if (nl.item(0) != null) {
				list.get(i).setContent(list.get(i).getContent() + "\n\\\n" + nl.item(0).getTextContent());
			}
		}

	}

	@Override
	public void parseCommenturl(List<WeiboData> list, Node dom, Component component, String... args) {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setCommentUrl("http://weibo.com/aj/comment/big?id=" + list.get(i).getMid());
		}
	}

	@Override
	public void parseRtturl(List<WeiboData> list, Node dom, Component component, String... args) {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setRttUrl("http://weibo.com/aj/mblog/info/big?id=" + list.get(i).getMid());
		}
	}

	@Override
	public void parsePubtime(List<WeiboData> list, Node dom, Component component, String... args) {
		NodeList nl = head(component.getXpath(), dom, list.size(), component.getName());
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setPubtime(nl.item(i).getTextContent());
			list.get(i).setPubdate(timeprocess(nl.item(i).getTextContent()));
		}
	}

	public Date timeprocess(String s) {
		Date d = super.timeProcess(s);
		if (d == null) {
			Calendar c = Calendar.getInstance();
			if (s.indexOf("月") > -1 || s.indexOf("日") > -1) {
				if (s.indexOf("年") == -1) {
					s = c.get(Calendar.YEAR) + "-" + s;
				}
				s = s.replace("年", "-").replace("月", "-").replace("日", "");
				d = super.timeProcess(s);
			}
			if (d == null) {

				int num = Integer.parseInt(StringUtil.extrator(s, "\\d"));
				if (s.contains("minute") || s.contains("分钟前")) {
					c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) - num);
				} else if (s.contains("hour") || s.contains("小时前")) {
					c.set(Calendar.HOUR, c.get(Calendar.HOUR) - num);
				} else if (s.contains("今天")) {
					s.replace("今天", c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE)
							+ "");
				} else if (s.contains("day") || s.contains("天前")) {
					c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - num);
				} else if (s.contains("month") || s.contains("月前")) {
					c.set(Calendar.MONTH, c.get(Calendar.MONTH) - num);
				} else if (s.contains("year") || s.contains("年前")) {
					c.set(Calendar.YEAR, c.get(Calendar.YEAR) - num);
				} else if (s.contains("second") || s.contains("秒前")) {
					c.set(Calendar.SECOND, c.get(Calendar.SECOND) - num);
				}
				return c.getTime();
			}
		}
		return d;
	}

}
