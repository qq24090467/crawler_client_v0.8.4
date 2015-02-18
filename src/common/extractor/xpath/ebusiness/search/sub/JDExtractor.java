package common.extractor.xpath.ebusiness.search.sub;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import oracle.net.aso.n;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.CommentData;
import common.bean.EbusinessData;
import common.bean.HtmlInfo;
import common.bean.OwnerData;
import common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor;
import common.siteinfo.CollectDataType;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.DOMUtil;
import common.util.JsonUtil;
import common.util.MD5Util;
import common.util.StringUtil;
import common.util.TimeUtil;
import common.http.SimpleHttpProcess;

public class JDExtractor extends EbusinessSearchXpathExtractor {

	public void templateOwnerPage(EbusinessData ed, HtmlInfo html, int page, String... keyword) {
		OwnerData od = new OwnerData();
		String ownerInfo = this.getOwnerInitUrl(ed.getInfo_code());

		if (ownerInfo == null)
			return;

		// 卖家名;卖家url;地址;jsonURL;卖家id
		String ownerName = ownerInfo.split(";")[0];
		String ownerUrl = ownerInfo.split(";")[1];
		String address = ownerInfo.split(";")[2];
		String ownerInitUrl = ownerInfo.split(";")[3];
		String ownerId = ownerInfo.split(";")[4];

		Systemconfig.sysLog.log("-----------------卖家抽取" + ownerInitUrl + "...---------------------");
		String content = null;

		try {
			SimpleHttpProcess http = new SimpleHttpProcess();
			content = http.getJsonContent(ownerInitUrl, "gbk");
			if (content == null) {
				Systemconfig.sysLog.log("卖家为京东商城.");
				od.setOwner_address("北京");
				od.setOwner_code("0");
				od.setOwner_company("京东商城");
				od.setOwner_product(ed.getInfo_code());
				od.setOwner_pScore("5");
				od.setOwner_sScore("5");
				od.setOwner_score("5");
				od.setOwner_url("http://www.jd.com");
				od.setMd5(MD5Util.MD5(od.getOwner_name()+od.getOwner_code()));
				ed.setOwner(od);
				return;
			}
			StringUtil.writeFile("filedown/Json/Owner/" + ownerName, content);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 抽取内容

		// args:ownerName,ownerUrl,address,ownerInitUrl,ownerId
		od.setOwner_name(ownerName);
		od.setOwner_url(ownerUrl);
		od.setOwner_address(address);
		od.setOwner_code(ownerId);
		//
		HtmlInfo h = new HtmlInfo();
		h.setType("DATA");
		h.setEncode("gbk");
		DOMUtil du = new DOMUtil();

		Node dom = du.ini(h.getContent(), "gbk");
		Component component = new Component();
		component.setXpath("");
		this.parseOwner_score(od, dom, component, new String[] { content });
		this.parseOwner_pScore(od, dom, component, new String[] { content });
		this.parseOwner_sScore(od, dom, component, new String[] { content });
		this.parseOwner_company(od, dom, component, new String[] { content });

		od.setOwner_product(ed.getInfo_code());
		od.setMd5(MD5Util.MD5(od.getOwner_name()+od.getOwner_code()));
		ed.setOwner(od);

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
			this.parseInfo_code(data, domtree, comp.getComponents().get("info_code"), html.getContent());

			// this.parseName(data, domtree, comp.getComponents().get("name"),
			// html.getContent());
			this.parsePrice(data, domtree, comp.getComponents().get("price"), html.getContent());
			this.parseImgs_product(data, domtree, comp.getComponents().get("imgs_product"), html.getContent());

			this.parseContent(data, domtree, comp.getComponents().get("content"), html.getContent());
			this.parseImgs_info(data, domtree, comp.getComponents().get("imgs_info"), html.getContent());
//			this.parseTransation(data, domtree, comp.getComponents().get("transation"), html.getContent());

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
			c.add(c.MONTH, -1);
			data.setUpdateDate(sdf4.format(c.getTime()));
			if (data.getMd5() == null) {
				data.setMd5(MD5Util.MD5(data.getUrl() + data.getUpdateDate()));
			}

			data.setIndexFlag_globle("flase");
			data.setIndexFlag_price("false");
			data.setIndexFlag_transaction("false");
			data.setIndexFlag_comments("false");

		}

		domtree = null;
		return owner_url;
	}

	SimpleHttpProcess shp = new SimpleHttpProcess();

	public void parseWidth(EbusinessData ebd, Node dom, Component component, String... args) {

		NodeList nl = commonList(component.getXpath(), dom);
		String params = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				params = nl.item(0).getTextContent();
				params = StringUtil.format(params);
			}
		}
		ebd.setParams_width(params.trim().replace("胎面宽度：", "").replace("宽度：", ""));
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
		ebd.setParams_diameter(params.trim().replace("轮毂尺寸：", "").replace("尺寸：", "").replace("寸", ""));
		Systemconfig.sysLog.log("diameter:" + params);
	}

	@Override
	public void parsePrice(EbusinessData ebd, Node dom, Component component, String... args) {
		// 从这个链接得到json文件，从中解析价格
		// http://p.3.cn/prices/mgets?type=1&skuIds=J_911850&area=1_72_2799_0&callback=jsonp1408513527122&_=1408513529642
		// jsonp1408513527122([{"id":"J_911850","p":"499.00","m":"880.00"}]);

		String oriUrl = ebd.getUrl();

		String id = oriUrl.substring(oriUrl.indexOf(".jd.com/") + 8);
		id = id.substring(0, id.indexOf(".html"));

		String url = "http://p.3.cn/prices/mgets?type=1&skuIds=J_" + id + "";

		TimeUtil.rest(2);

		String content = shp.getJsonContent(url, "utf-8");
		if (content == null) {
			System.out.println("解析price时没有抓取到json内容。");
			return;
		}

		ebd.setPrice(JsonUtil.getStringByKey(content, "p"));
	}

	@Override
	public void parseInfo_code(EbusinessData ebd, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub
		String oriUrl = ebd.getUrl();

		String info_code = oriUrl.substring(oriUrl.indexOf(".jd.com/") + 8);
		info_code = info_code.substring(0, info_code.indexOf(".html"));

		ebd.setInfo_code(info_code);

	}

	@Override
	public String getOwnerInitUrl(String productId) {

		String sellerIDUrl = "http://st.3.cn/gvi.html?type=popdeliver&skuid=" + productId;

		SimpleHttpProcess shp = new SimpleHttpProcess();
		String content = shp.getJsonContent(sellerIDUrl, "gbk");

		// content = StringUtil.regMatcher(content, "\\(", "\\)");
		if (content == null) {
			return null;
		}
		String sellerId = JsonUtil.getStringByKey(content, "vid");

		if (sellerId.equals("0") || sellerId.equals(0)) {
			Systemconfig.sysLog.log("此为京东自营商品.");
			String sellerName = "京东商城";
			String address = "北京";
			String sellerUrl = "www.jd.com";

			String ownerUrl = sellerName + ";" + sellerUrl + ";" + address + ";"
					+ "http://rms.shop.jd.com/json/popscore/scorefact.action?venderID=" + sellerId + ";" + sellerId;
			return ownerUrl;
		}

		String sellerName = JsonUtil.getStringByKey(content, "vender");
		String address = JsonUtil.getStringByKey(content, "df");
		String sellerUrl = JsonUtil.getStringByKey(content, "url");

		// 卖家名;卖家url;地址;jsonURL;卖家id
		String ownerUrl = sellerName + ";" + sellerUrl + ";" + address + ";"
				+ "http://rms.shop.jd.com/json/popscore/scorefact.action?venderID=" + sellerId + ";" + sellerId;

		return ownerUrl;
	}

	@Override
	public int getCommentCount(String content) {
		// TODO Auto-generated method stub
		int commentCount = 0;
		try {
			commentCount = Integer.parseInt(JsonUtil.getStringByKey(
					JsonUtil.getStringByKey(content, "productCommentSummary"), "commentCount"));

		} catch (Exception e) {
			// TODO: handle exception
			Systemconfig.sysLog.log("获取评论数时出现异常.");
		}

		return commentCount;
	}

	/**
	 * @param flag
	 *            0:jd, 1:taobao/tmall
	 * @return
	 */
	public String getCommentInitUrl(EbusinessData data) {
		String time = "" + System.currentTimeMillis();
		String callback = "?callback=CommentListNew.setData";
		if (data.getInfo_code() == null || data.getInfo_code().equals(""))
			return null;
//		String url = "http://club.jd.com/productpage/p-" + data.getInfo_code() + "-s-0-t-3-p-0.html" + "?_=" + time;
		String url = "http://club.jd.com/productpage/p-" + data.getInfo_code() + "-s-0-t-3-p-0.html" + callback;
	
		return url;
	}

	@Override
	public String getCommentNext(String currUrl) {
		String strCurrPage = currUrl.substring(currUrl.lastIndexOf("-p-") + 3, currUrl.lastIndexOf(".html"));

		int intNext = -1;
		try {
			intNext = Integer.parseInt(strCurrPage) + 1;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (intNext == -1) {
			System.out.println("解析下一页url出现问题: 没有从当前url得到正确的页码。");
			return null;
		}

		String nextUrl = currUrl.replace("-p-" + strCurrPage + ".html", "-p-" + intNext + ".html");

//		String time = "" + System.currentTimeMillis();
		
		nextUrl = nextUrl.substring(0, nextUrl.lastIndexOf("?")) + "?callback=CommentListNew.setData";
		return nextUrl;

	}

	/**
	 * @param ed
	 * @param args
	 *            [0]: url
	 * @param commentPage
	 *            当前页码
	 * @return 评论下一页url
	 */
	@Override
	public String templateCommentPage(EbusinessData ed, int commentPage, String... args) {
		String url = args[0];

		JSONArray jArray = null;

		String content = shp.getJsonContent(url, "utf-8");

		if (content == null) {
			Systemconfig.sysLog.log("没有抓取到评论页内容：" + url);
			return null;
		}

		int commentCount = 0;
		int pageCount = 0;
		try {

			commentCount = this.getCommentCount(content);

			ed.setTransation(commentCount + "");

			if (commentCount == 0) {// 没有评论
				Systemconfig.sysLog.log("评论页解析到评论数为0.");
				return null;
			}
			pageCount = commentCount / 10 + 1;

			JSONObject jObject = JSONObject.fromObject(content);

			jArray = jObject.getJSONArray("comments");

		} catch (Exception e) {
			Systemconfig.sysLog.log("获得评论列表出现异常.");
			return null;
		}

		if (jArray.size() == 0) {
			Systemconfig.sysLog.log("本页已没有评论内容，本商品评论采集结束。");
			return null;// 最后页
		}
		Systemconfig.sysLog.log("获得评论列表.");
		for (int i = 0; i < jArray.size(); i++) { // 一页中的每一条评论
			try {

				Systemconfig.sysLog.log("获取第:" + (10 * commentPage + i) + "条评论...(评论总数:" + commentCount + ").");
				JSONObject comment = jArray.getJSONObject(i);

				String id = comment.getString("id");
				String person = comment.getString("nickname");
				String level = comment.getString("userLevelName");

				String info = comment.getString("content");
				String pubtime = comment.getString("creationTime");
				String label = "";

				String product = comment.getString("productSize");// 型号
				String score = comment.getString("score");

				Systemconfig.sysLog.log("内容:" + info);

				CommentData cd = new CommentData();
				cd.setComment_id(id);
				cd.setComment_person(person);
				cd.setComment_level(level);
				cd.setComment_info(info);
				cd.setComment_pubtime(pubtime);
				cd.setComment_label(label);
				cd.setComment_product(product);
				cd.setComment_score(score);

				if (ed.getComments() == null) {
					List<CommentData> comments = new ArrayList<CommentData>();
					comments.add(cd);
					ed.setComments(comments);
				} else {
					ed.getComments().add(cd);
				}
			} catch (Exception e) {
				// TODO: handle exception
				Systemconfig.sysLog.log("获取第:" + (10 * commentPage + i) + "条评论发生异常.");
			}

		}// end for

		String nextUrl = getCommentNext(url);

		// if (commentPage >= pageCount)
		// return null;
		return nextUrl;
	}

	/*
	 * args[0] json文件内容
	 * 
	 * @see
	 * common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor
	 * #parseOwner_address(common.bean.EbusinessData, org.w3c.dom.Node,
	 * common.siteinfo.Component, java.lang.String[])
	 */
	@Override
	public void parseOwner_address(EbusinessData ebd, Node dom, Component component, String... args) {

	}

	/*
	 * args[0] json文件内容
	 * 
	 * @see
	 * common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor
	 * #parseOwner_company(common.bean.EbusinessData, org.w3c.dom.Node,
	 * common.siteinfo.Component, java.lang.String[])
	 */
	public void parseOwner_company(OwnerData od, Node dom, Component component, String... args) {
		String score = JsonUtil.getStringByKey(args[0], "f4");
		od.setOwner_company(score);
	}

	/*
	 * args[0] json文件内容
	 * 
	 * @see
	 * common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor
	 * #parseOwner_name(common.bean.EbusinessData, org.w3c.dom.Node,
	 * common.siteinfo.Component, java.lang.String[])
	 */
	@Override
	public void parseOwner_name(EbusinessData ebd, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub
		super.parseOwner_name(ebd, dom, component, args);
	}

	/*
	 * args[0] json文件内容
	 * 
	 * @see
	 * common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor
	 * #parseOwner_pScore(common.bean.EbusinessData, org.w3c.dom.Node,
	 * common.siteinfo.Component, java.lang.String[])
	 */
	public void parseOwner_pScore(OwnerData od, Node dom, Component component, String... args) {
		String score = JsonUtil.getStringByKey(args[0], "f20");
		od.setOwner_pScore(score);
	}

	/*
	 * args[0] json文件内容
	 * 
	 * @see
	 * common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor
	 * #parseOwner_score(common.bean.EbusinessData, org.w3c.dom.Node,
	 * common.siteinfo.Component, java.lang.String[])
	 */
	public void parseOwner_score(OwnerData od, Node dom, Component component, String... args) {
		String score = JsonUtil.getStringByKey(args[0], "f23");
		od.setOwner_score(score);
	}

	/*
	 * args[0] json文件内容w
	 * 
	 * @see
	 * common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor
	 * #parseOwner_sScore(common.bean.EbusinessData, org.w3c.dom.Node,
	 * common.siteinfo.Component, java.lang.String[])
	 */
	public void parseOwner_sScore(OwnerData od, Node dom, Component component, String... args) {
		String score = JsonUtil.getStringByKey(args[0], "f22");
		od.setOwner_sScore(score);
	}

	@Override
	public String parseOwner_url(EbusinessData ebd, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub
		return super.parseOwner_url(ebd, dom, component, args);
	}

}
