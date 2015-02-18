package common.extractor.xpath;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.CommonData;
import common.bean.EbusinessData;
import common.bean.HtmlInfo;
import common.extractor.AbstractExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.DOMUtil;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * Xpath抽取类
 * 
 * @author grs
 */
public abstract class XpathExtractor<T> extends AbstractExtractor<T> {

	/**
	 * 供测试
	 * 
	 * @param ebd
	 * @param dom
	 * @param component
	 * @param args
	 */
	public String testPageParse(CommonData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String params = "";
		if (nl != null) {
			for (int i = 0; i < nl.getLength(); i++) {
				params += nl.item(i).getTextContent() + "\r\n";
			}
		}
		params = StringUtil.format(params);
		// System.out.println("result:");
		System.out.println(params);
		return params;
	}

	public String testListParse(List<CommonData> list, Node dom, Component component, String... args) {
		if (component == null)
			return null;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return null;
		String params = "";
		for (int i = 0; i < nl.getLength(); i++) {
			// System.out.print("[" + i + "]:");
			System.out.println(StringUtil.format(nl.item(i).getTextContent()));
			params += StringUtil.format(nl.item(i).getTextContent()) + "\r\n";
		}
		return params;

	}

	/**
	 * 检测属性列表数量一致性
	 * 
	 * @param len1
	 * @param len2
	 * @param s
	 * @param first
	 */
	protected void judge(int len1, int len2, String s, boolean first) {
		if (first)
			return;
		if (len1 != len2) {
			System.err.println("抽取" + s + "属性数量不一致");
		}
	}

	protected void judge(int len1, int len2, String s) {
		judge(len1, len2, s, false);
	}

	protected void judge(int len1, int len2) {
		judge(len1, len2, "", true);
	}

	/**
	 * 通用的xpath抽取方法
	 * 
	 * @param xpath
	 * @param domtree
	 * @return
	 */
	protected NodeList commonList(String xpath, Node domtree) {
		if (xpath == null || xpath.equals("") || xpath.startsWith("${"))
			return null;
		NodeList list = null;
		try {
			list = XPathAPI.selectNodeList(domtree, xpath);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 列表解析，第一个属性
	 * 
	 * @param xpath
	 * @param dom
	 * @return
	 */
	protected NodeList head(String xpath, Node dom) {
		return commonList(xpath, dom);
	}

	/**
	 * 列表页属性解析，需验证属性
	 * 
	 * @param xpath
	 * @param dom
	 * @param size
	 * @param flag
	 * @return
	 */
	protected NodeList head(String xpath, Node dom, int size, String flag) {
		NodeList nl = commonList(xpath, dom);
		if (nl == null)
			return null;
		if (size > 0) {
			if (flag == null)
				judge(size, nl.getLength());
			else
				judge(size, nl.getLength(), flag);
		}
		return nl;
	}

	/**
	 * Url类型的抽取处理
	 * 
	 * @param component
	 * @param nl
	 * @return
	 */
	protected String urlProcess(Component component, Node nl) {
		String url = nl.getTextContent();
		if (!url.startsWith("http")) {
			if (component.getPrefix() != null && !component.getPrefix().startsWith("${"))
				url = component.getPrefix() + url;
		}
		if (component.getPostfix() != null && !component.getPostfix().startsWith("${"))
			url = url + component.getPostfix();

		return url;
	}

	protected DOMUtil domUtil = new DOMUtil();

	/**
	 * 获得真实内容
	 * 
	 * @param content
	 * @param siteinfo
	 * @return
	 */
	protected Node getRealDOM(HtmlInfo html) {
		return domUtil.ini(html.getContent(), html.getEncode());
	}

	/**
	 * 获得真实组件信息
	 * 
	 * @param mode
	 * @param siteinfo
	 * @return
	 */
	protected CommonComponent getRealComp(Siteinfo siteinfo, String mode) {
		return siteinfo.getCommonComponent().get(mode);
	}

	@Override
	public String templateContentPage(T data, HtmlInfo html, int page, String... keyword) {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		// String domcontent = domtree.getTextContent();
		// System.out.println(domcontent);
		if (domtree == null) {
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType()
				.substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件
		processPage(data, domtree, comp.getComponents(), html.getContent());

		return parsePageNext(domtree, comp.getComponents().get("page_next"));
	}

	/**
	 * 内容页属性抽取
	 * 
	 * @param data
	 * @param domtree
	 * @param map
	 * @param args
	 */
	public abstract void processPage(T data, Node domtree, Map<String, Component> map, String... args);

	/**
	 * 解析内容页的下一页
	 * 
	 * @param domtree
	 * @param component
	 * @param strings
	 * @return
	 */
	public String parsePageNext(Node domtree, Component component, String... args) {
		if (component == null)
			return null;
		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl == null)
			return null;
		if (nl.item(0) != null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}

	@Override
	public String templateContentPage(T data, HtmlInfo html, String... keyword) {
		return templateContentPage(data, html, 1, keyword);
	}

	@Override
	public String templateListPage(List<T> list, HtmlInfo html, int page, String... keyword) {
		list.clear();
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if (domtree == null) {
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType()
				.substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件
		processList(list, domtree, comp.getComponents(),
				args(html.getContent(), String.valueOf(siteinfo.getSiteFlag()), keyword));
		if (list.size() == 0)
			return null;
		attrSet(list, siteinfo.getSiteFlag(), keyword[0], Integer.parseInt(keyword[2]));
		return parseNext(domtree, comp.getComponents().get("next"), new String[] { keyword[1], page + "" });
	}

	/**
	 * 列表页面属性抽取
	 * 
	 * @param list
	 * @param domtree
	 * @param components
	 * @param args
	 */
	public abstract void processList(List<T> list, Node domtree, Map<String, Component> components, String... args);

	/**
	 * 共有属性设置
	 * 
	 * @param list
	 * @param siteflag
	 * @param key
	 * @param code
	 */
	protected void attrSet(List<T> list, int siteflag, String key, int code) {
		for (T t : list) {
			CommonData cd = (CommonData) t;
			cd.setSearchKey(key);
			cd.setCategoryCode(code);
			cd.setMd5(MD5Util.MD5(cd.getUrl()));
			cd.setSiteId(siteflag);
		}
	}

	/**
	 * 解析列表页的下一页
	 * 
	 * @param domtree
	 * @param component
	 * @param strings
	 * @return
	 */
	public String parseNext(Node domtree, Component component, String... args) {
		if (component == null)
			return null;
		NodeList nl = commonList(component.getXpath(), domtree);
		if (nl == null)
			return null;
		if (nl.item(0) != null) {
			return urlProcess(component, nl.item(0));
		}
		return null;
	}

	/**
	 * 0: content 1: siteflag ...
	 * 
	 * @param content
	 * @param siteflag
	 * @param keyword
	 * @return
	 */
	private String[] args(String content, String siteflag, String... keyword) {
		String arr[] = new String[keyword.length + 1];
		arr[0] = content;
		arr[1] = siteflag;
		for (int i = 2; i < keyword.length; i++) {
			arr[i] = keyword[i - 2];
		}
		return arr;
	}

}
