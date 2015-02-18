package common.extractor.xpath.ebusiness.search;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.HtmlInfo;
import common.bean.EbusinessData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.CollectDataType;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * 电商抽取实现类
 * 
 * @author grs
 */
public class EbusinessSearchXpathExtractor extends XpathExtractor<EbusinessData> implements
		EbusinessSearchExtractorAttribute {

	@Override
	public String templateListPage(List<EbusinessData> list, HtmlInfo html, int currPage, String... keyword) {
		// keyword[0]:keyword
		// keyword[1]:currenturl
		// keyword[2]:
		list.clear();
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		Node domtree = getRealDOM(html);
		if (domtree == null) {
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, html.getType().substring(0, html.getType().indexOf(File.separator)));// 得到元数据的配置组件

		this.parseTitle(list, domtree, comp.getComponents().get("title"));

		if (list.size() == 0)
			return null;

		this.parseUrl(list, domtree, comp.getComponents().get("url"), html.getContent());
		this.parsePrice(list, domtree, comp.getComponents().get("price"), html.getContent());
		this.parseTransation(list, domtree, comp.getComponents().get("transation"), html.getContent());

		for (EbusinessData vd : list) {
			vd.setSearchKey(keyword[0]);
			vd.setCategoryCode(Integer.parseInt(keyword[2]));
			
			Date date = new Date();
			Calendar c = new GregorianCalendar();
			c.setTime(date);
			c.add(c.MONTH, -1);
			vd.setUpdateDate(sdf4.format(c.getTime()));
			
			vd.setMd5(MD5Util.MD5(vd.getUrl() + vd.getUpdateDate()));
			vd.setSiteId(siteinfo.getSiteFlag());
		}
		String nextPage = parseNext(domtree, comp.getComponents().get("next"), new String[] { keyword[1], currPage + "" });
		domtree = null;
		return nextPage;
	}

	@Override
	public String templateContentPage(EbusinessData data, HtmlInfo html, int page, String... keyword) {

		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
		// create(content);
		Node domtree = getRealDOM(html);
		if (domtree == null) {
			Systemconfig.sysLog.log("DOM解析为NULL！！");
			return null;
		}
		CommonComponent comp = getRealComp(siteinfo, CollectDataType.DATA.name());// 得到元数据的配置组件
		String owner_url = "";
		if (page == 1) {
			// Systemconfig.sysLog.log("------------extract content page..------------");
			// this.parseInfo_code(data, domtree,
			// comp.getComponents().get("info_code"), html.getContent());

			// this.parseName(data, domtree, comp.getComponents().get("name"),
			// html.getContent());
			// this.parsePrice(data, domtree, comp.getComponents().get("price"),
			// html.getContent());
			// this.parseImgs_product(data, domtree,
			// comp.getComponents().get("imgs_product"), html.getContent());

			this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
			this.parseImgs_info(data, domtree, comp.getComponents().get("imgs_info"), html.getContent());
			this.parseTransation(data, domtree, comp.getComponents().get("transation"), html.getContent());

			this.parseInfo_pubtime(data, domtree, comp.getComponents().get("info_pubtime"), html.getContent());
			this.parseInfo_type(data, domtree, comp.getComponents().get("info_type"), new String[] { html.getContent() });
			this.parseParams(data, domtree, comp.getComponents().get("params"), new String[] { html.getContent() });
			this.parseBrand(data, domtree, comp.getComponents().get("brand"), html.getContent());
			this.parseWidth(data, domtree, comp.getComponents().get("width"), new String[] { html.getContent() });
			this.parseDiameter(data, domtree, comp.getComponents().get("diameter"), new String[] { html.getContent() });
			this.parseModel(data, domtree, comp.getComponents().get("model"), new String[] { html.getContent() });

			// this.parseOwner_code(data, domtree, comp.getComponents().get(""),
			// html.getContent());
			// this.parseOwner_address(data, domtree,
			// comp.getComponents().get("owner_address"), html.getContent());
			this.parseOwner_company(data, domtree, comp.getComponents().get("owner_company"), html.getContent());
			// this.parseOwner_name(data, domtree,
			// comp.getComponents().get("owner_name"), html.getContent());
			// this.parseOwner_pScore(data, domtree,
			// comp.getComponents().get("owner_pscore"), html.getContent());
			// this.parseOwner_score(data, domtree,
			// comp.getComponents().get("owner_score"), html.getContent());
			// this.parseOwner_sScore(data, domtree,
			// comp.getComponents().get("owner_sscore"), html.getContent());
			// this.parseOwner_url(data, domtree,
			// comp.getComponents().get("owner_url"), html.getContent());
			if (data.getOwner() != null)
				data.getOwner().setOwner_product(data.getInfo_code());

			owner_url = this.parseOwner_url(data, domtree, comp.getComponents().get("owner_url"),
					new String[] { html.getContent() });

			/* set目标品牌 */
			for (String brandCode : Systemconfig.ebusinessBrandCode.keySet()) {
				if (data.getSearchKey().contains(brandCode)) {
					data.setCompany(Systemconfig.ebusinessBrandCode.get(brandCode));
					break;
				}
			}
			if (data.getCompany() == null)
				data.setCompany(data.getSearchKey().replace("轮胎", "").trim());

			data.setInserttime(new Date());
			data.setSiteId(siteinfo.getSiteFlag());

			Date date = new Date();
			Calendar c = new GregorianCalendar();
			c.setTime(date);
			c.add(Calendar.MONTH, -1);
			data.setUpdateDate(sdf4.format(c.getTime()));
			if (data.getMd5() == null)
				data.setMd5(MD5Util.MD5(data.getUrl() + data.getUpdateDate()));

			data.setIndexFlag_globle("flase");
			data.setIndexFlag_price("false");
			data.setIndexFlag_transaction("false");
			data.setIndexFlag_comments("false");

		}

		domtree = null;
		return owner_url;
	}

	@Override
	public void parseUrl(List<EbusinessData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		judge(list.size(), nl.getLength(), "url");
		for (int i = 0; i < nl.getLength(); i++) {
			String url = nl.item(i).getTextContent();
			if (!url.startsWith("http://")) {
				if (component.getPrefix() != null && !component.getPrefix().startsWith("${"))
					url = component.getPrefix() + url;
			}
			if (component.getPostfix() != null && !component.getPostfix().startsWith("${"))
				url = url + component.getPostfix();
			list.get(i).setUrl(url);
		}
	}

	/**
	 * 从列表页获取价格的方法
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePrice(List<EbusinessData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		judge(list.size(), nl.getLength(), "price");
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setPrice(StringUtil.format(nl.item(i).getTextContent().replace("¥", "")));
		}
	}

	/**
	 * 从列表页获取销量的方法
	 * 
	 * @param list
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseTransation(List<EbusinessData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		judge(list.size(), nl.getLength(), "transation");
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i)
					.setTransation(StringUtil.format(nl.item(i).getTextContent().replace("人付款", "").replace("人收货", "").replace("个评论", "")));
		}
	}

	public void parseTransation(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String transition = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				transition = nl.item(0).getTextContent();
			}
			ebd.setTransation(transition.trim());
			Systemconfig.sysLog.log("transition:" + transition);
		}
	}

	public void parseImgs_product(List<EbusinessData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		judge(list.size(), nl.getLength(), "imgs_product");
		for (int i = 0; i < nl.getLength(); i++) {
			list.get(i).setImgs_product(StringUtil.format(nl.item(i).getTextContent()));
		}
	}

	@Override
	public void parseTitle(List<EbusinessData> list, Node dom, Component component, String... args) {
		if (component == null)
			return;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;
		judge(list.size(), nl.getLength());
		for (int i = 0; i < nl.getLength(); i++) {
			EbusinessData vd = new EbusinessData();
			vd.setTitle(StringUtil.format(nl.item(i).getTextContent()));
			list.add(vd);
		}
	}

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		if (component == null)
			return null;
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return null;
		String url = null;
		if (nl.item(0) != null) {
			url = nl.item(0).getTextContent();
			if (!url.startsWith("http://")) {
				if (component.getPrefix() != null && !component.getPrefix().startsWith("${"))
					url = component.getPrefix() + url;
			}
			if (component.getPostfix() != null && !component.getPostfix().startsWith("${"))
				url = url + component.getPostfix();
		}
		return url;
	}

	public void parseBrand(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String brand = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				brand = nl.item(0).getTextContent();
			}
		}
		ebd.setBrand(brand.trim());
		Systemconfig.sysLog.log("brand:" + brand);
	}

	public void parseImgs_info(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String result = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				for (int i = 0; i < nl.getLength(); i++) {
					result += nl.item(i).getTextContent().replace("data-lazyload=\"", "").replace("src=\"", "")
							.replace("\"", "")
							+ ";";
				}
			}
		}
		ebd.setImgs_info(result);
	}

	public void parseContent(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String result = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				for (int i = 0; i < nl.getLength(); i++) {
					result += nl.item(i).getTextContent() + ";";
				}
			}
		}
		ebd.setContent(result);
	}

	public void parsePrice(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String price = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				price = nl.item(0).getTextContent();
			} else {
				return;
			}
		} else {
			return;
		}
		ebd.setPrice(price.trim());
		Systemconfig.sysLog.log("price:" + price);
	}

	public void parseImgs_product(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String imgs_product = "";
		if (nl != null) {
			for (int i = 0; i < nl.getLength(); i++) {
				imgs_product += nl.item(i).getTextContent() + ";";
			}
		}
		ebd.setImgs_product(imgs_product.trim());
		Systemconfig.sysLog.log("imgs:" + imgs_product);
	}

	public void parseName(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String name = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				name = nl.item(0).getTextContent();
			}
			ebd.setName(name.trim());
			Systemconfig.sysLog.log("name:" + name);
		}
	}

	public void parseParams(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String params = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				for (int i = 0; i < nl.getLength(); i++) {
					params += nl.item(i).getTextContent() + " ";
				}
				// params = StringUtil.format(params);
				params = StringUtil.formatRetain1Space(params);
			}
		}
		ebd.setParams_params(params.trim());
		Systemconfig.sysLog.log("params:" + params);
	}

	public void parseModel(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String params = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				params = nl.item(0).getTextContent();
				params = StringUtil.format(params);
			}
		}

		ebd.setParams_model(params.trim());
		Systemconfig.sysLog.log("model:" + params);
	}

	public void parseWidth(EbusinessData ebd, Node dom, Component component, String... args) {

		NodeList nl = commonList(component.getXpath(), dom);
		String params = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				params = nl.item(0).getTextContent();
				params = StringUtil.format(params);
			}
		}

		ebd.setParams_width(params.trim());
		Systemconfig.sysLog.log("width:" + params);
	}

	public void parseDiameter(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String params = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				params = nl.item(0).getTextContent();
				params = StringUtil.format(params);
			}
		}
		ebd.setParams_diameter(params.trim());
		Systemconfig.sysLog.log("diameter:" + params);
	}

	@Override
	public void parseInfo_code(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	@Override
	public void parseInfo_pubtime(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String info_pubtime = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				info_pubtime = nl.item(0).getTextContent().replace("：", ":");
				if (info_pubtime.contains(":"))
					info_pubtime = info_pubtime.substring(info_pubtime.indexOf(":") + 1);
			}
		}
		ebd.setInfo_pubtime(info_pubtime.trim());
		Systemconfig.sysLog.log("info_pubtime:" + info_pubtime);
	}

	@Override
	public void parseInfo_type(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String info_type = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				info_type = nl.item(0).getTextContent().replace("类别:", "").replace("类别：", "");
			}
		}
		ebd.setInfo_type(info_type.trim());
		Systemconfig.sysLog.log("info_type:" + info_type);

	}

	public void parseCategoryCode(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	public void parseSiteId(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	public void parseList(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	@Override
	public int getCommentCount(String content) {
		return 0;
	}

	public String parseOwner_url(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String owner_url = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				owner_url = nl.item(0).getTextContent();
			}
		}

		Systemconfig.sysLog.log("owner_url:" + owner_url);
		return owner_url;
	}

	/**
	 * 抽取卖家数据 jd从json中抽取 taobao/tmall直接在页面抽取
	 * 
	 * @param data
	 * @param html
	 * @param page
	 * @param keyword
	 */
	public void templateOwnerPage(EbusinessData data, HtmlInfo html, int page, String... keyword) {

	}

	public void parseOwner_score(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	public void parseOwner_pScore(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	public void parseOwner_sScore(EbusinessData ebd, Node dom, Component component, String... args) {
	}

	public void parseOwner_company(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	public void parseOwner_address(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	public void parseOwner_name(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	public void parseOwner_code(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	/*
	 * 卖家入口url
	 * 
	 * @see
	 * common.extractor.xpath.ebusiness.search.EbusinessSearchExtractorAttribute
	 * #getOwnerInitUrl(java.lang.String)
	 */
	public String getOwnerInitUrl(String productId) {
		return null;
	}

	/**
	 * 评论入口url
	 * 
	 * @param productId
	 * @return
	 */
	public String getCommentInitUrl(EbusinessData data) {
		return null;
	}

	/**
	 * @param ed
	 * @param jsonContent
	 *            json文件的内容
	 * @param commentPage
	 *            当前页码
	 * @return 评论下一页url
	 */
	public String templateCommentPage(EbusinessData ed, int commentPage, String... args) {

		return null;
	}

	/**
	 * 获取商品评论入口url 京东评论url不需要卖家code 淘宝需要商品code和卖家code
	 * 
	 * @param info_code
	 *            商品码
	 * @param owner_code
	 *            卖家码
	 * @param commentPage
	 *            当前评论页码
	 * @param flag
	 *            jd(0) or tamll(1) or taobao(2) ,,, 主要用来区分天猫和淘宝
	 * @return
	 */
	public String getCommentInitUrl(String info_code, String owner_code, int commentPage, int flag) {
		return null;
	}

	
	public String getCommentNext(String currUrl) {
		return null;
	}

	@Override
	public void processPage(EbusinessData data, Node domtree, Map<String, Component> map, String... args) {

	}

	@Override
	public void processList(List<EbusinessData> list, Node domtree, Map<String, Component> components, String... args) {
	}
}
