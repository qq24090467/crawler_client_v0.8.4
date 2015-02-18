package common.extractor.xpath.ebusiness.monitor.sub;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

import common.bean.CommentData;
import common.bean.EbusinessData;
import common.bean.HtmlInfo;
import common.bean.OwnerData;
import common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor;
import common.siteinfo.CommonComponent;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.JsonUtil;
import common.util.MD5Util;
import common.util.StringUtil;
import common.http.SimpleHttpProcess;

public class TaobaoExtractor extends EbusinessSearchXpathExtractor {

	SimpleHttpProcess shp = new SimpleHttpProcess();

	private void processHtml(HtmlInfo html) {
		String content = html.getContent();
		if (!content.contains("{") || !content.contains("}")) {
			html.setContent(null);
			return;
		}
		content = content.substring(content.indexOf("{"));
		content = content.substring(0, content.lastIndexOf("}") + 1);
		html.setContent(content);

	}

	@Override
	public void parseTitle(List<EbusinessData> list, Node dom, Component component, String... args) {

		// System.out.println(args[0]);
		JSONObject jObject = JSONObject.fromObject(args[0]);

		JSONObject mods = jObject.getJSONObject("mods");
		JSONObject itemlist = mods.getJSONObject("itemlist");

		JSONObject data = itemlist.getJSONObject("data");
		JSONArray auctions = data.getJSONArray("auctions");
		String tmp = "";
		for (int i = 0; i < auctions.size(); i++) {
			JSONObject product = auctions.getJSONObject(i);// 每一个商品
			String title = product.getString("raw_title");
			String url = product.getString("detail_url");
			String productId = product.getString("nid");
			String location = product.getString("item_loc");
			String comm_url = product.getString("comment_url");
			String price = product.getString("view_price");
			String view_sales = product.getString("view_sales").replace("人付款", "").replace("人收货", "");
			tmp += view_sales + " ";
			try {
				if (Integer.parseInt(view_sales) <= Systemconfig.forceStopSales)
					Systemconfig.forceStop = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			String sellerName = product.getString("nick");
			String sellerId = product.getString("user_id");
			String deliveryScore = product.getJSONObject("shopcard").getString("delivery");
			String serviceScore = product.getJSONObject("shopcard").getString("service");
			String descriptionScore = product.getJSONObject("shopcard").getString("description");
			try {
				deliveryScore = deliveryScore.substring(1, 3);
				deliveryScore = (Double.parseDouble(deliveryScore) / 10) + "";

				serviceScore = serviceScore.substring(1, 3);
				serviceScore = (Double.parseDouble(serviceScore) / 10) + "";

				descriptionScore = descriptionScore.substring(1, 3);
				descriptionScore = (Double.parseDouble(descriptionScore) / 10) + "";
			} catch (Exception e) {

			}
			String sellerUrl = product.getString("shopLink");
			String imgUrl = product.getString("pic_url");

			if (title == null || url == null)
				break;

			EbusinessData ed = new EbusinessData();
			ed.setTitle(title);
			ed.setUrl(url);
			ed.setInfo_code(productId);
			ed.setPrice(price);
			ed.setImgs_product(imgUrl);

			OwnerData od = new OwnerData();
			od.setOwner_address(location);
			od.setOwner_name(sellerName);
			// od.setId(Integer.parseInt(sellerId));
			od.setOwner_code(sellerId);
			od.setOwner_pScore(deliveryScore);
			od.setOwner_score(serviceScore);
			od.setOwner_sScore(descriptionScore);
			od.setOwner_url(sellerUrl);
			od.setMd5(MD5Util.MD5(od.getOwner_name()+od.getOwner_code()));

			ed.setOwner(od);
			list.add(ed);

		}
		Systemconfig.sysLog.log("本页产品成交量依次为：" + tmp);
	};

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

		processHtml(html);
		if (html.getContent() == null)
			return null;
		this.parseTitle(list, domtree, comp.getComponents().get("title"), html.getContent());

		if (list.size() == 0)
			return null;

		// this.parseUrl(list, domtree, comp.getComponents().get("url"),
		// html.getContent());
		// this.parsePrice(list, domtree, comp.getComponents().get("price"),
		// html.getContent());
		// this.parseTransation(list, domtree,
		// comp.getComponents().get("transation"), html.getContent());

		// int k=0;
		for (EbusinessData vd : list) {
			// System.out.println(k++);
			vd.setSearchKey(keyword[0]);
			vd.setCategoryCode(Integer.parseInt(keyword[2]));
			Date date = new Date();
			Calendar c = new GregorianCalendar();
			c.setTime(date);
			c.add(c.MONTH, -1);
			vd.setUpdateDate(sdf4.format(c.getTime()));
			vd.setMd5(MD5Util.MD5(vd.getUrl()+vd.getUpdateDate()));
			vd.setSiteId(siteinfo.getSiteFlag());
		}
		String nextPage = parseNext(domtree, comp.getComponents().get("next"), new String[] { keyword[1], currPage + "" });
		domtree = null;
		return nextPage;

	}

	@Override
	public String parseNext(Node dom, Component component, String... args) {

		String currUrl = args[0];
		String nextUrl = "";

		if (currUrl.contains("&s=")) {

			String currPageStr = StringUtil.regMatcher(currUrl, "&s=", "&");
			if (currPageStr == null)
				currPageStr = currUrl.substring(currUrl.lastIndexOf("&s=") + 3);
			int nextPageInt = Integer.parseInt(currPageStr) + 44;
			nextUrl = currUrl.replace("&s=" + currPageStr, "&s=" + nextPageInt);
		} else {
			nextUrl = currUrl + "&tab=all&bcoffset=1&s=44";
		}

		return nextUrl;
	}

	@Override
	public void parseBrand(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String params = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				params = nl.item(0).getTextContent();
				if (params.contains(":"))
					params = params.substring(params.indexOf(":") + 1);
				else if (params.contains("："))
					params = params.substring(params.indexOf("：") + 1);
			}
		}

		if (params.equals("") && ebd.getParams_params() != null) {

			if (ebd.getParams_params().contains("品牌")) {
				params = ebd.getParams_params();
				params = StringUtil.regMatcher(params, "品牌", "[颜轮规型胎]");
				if (params == null)
					return;
				if (params.contains(":"))
					params = params.replace(":", "");
				if (params.contains("："))
					params = params.replace("：", "");
			}
		}
		ebd.setBrand(StringUtil.format(params));
		Systemconfig.sysLog.log("brand:" + params);
	}

	@Override
	public void parseTransation(EbusinessData ebd, Node dom, Component component, String... args) {
		String htmlContent = args[0];
		String transUrl = "";
		String sales = "";

		HtmlInfo html = new HtmlInfo();
		html.setEncode("gbk");
		html.setType("html");
		html.setReferUrl("http://detail.tmall.com/item.htm?id=36307184163&abbucket=0");
		html.setCookie("sca=5d36d2b3; tbsa=e97e69911da5992ba470d7b4_1414720919_4; cna=v0jXDKH/KBUCAZ/isbxlMFll; atpsida=61abbc42899c75ad437705f5_1414720932; cmida=1401053355_20141031100212");

		if (ebd.getUrl().startsWith("http://detail.tmall.com")) {
			transUrl = StringUtil.regMatcher(htmlContent, "  var l,url='", "';");

			html.setOrignUrl(transUrl);
			shp.getContent(html);

			// System.out.println(html.getContent());

			JSONObject json = JSONObject.fromObject(html.getContent());
			JSONObject defaultModel = json.getJSONObject("defaultModel");
			JSONObject sellCountDO = defaultModel.getJSONObject("sellCountDO");
			sales = sellCountDO.getString("sellCount");
		} else if (ebd.getUrl().startsWith("http://item.taobao.com")) {
			transUrl = StringUtil.regMatcher(htmlContent, "\"apiItemInfo\":\"", "\",");

			html.setOrignUrl(transUrl);
			shp.getContent(html);
			// System.out.println(html.getContent());

			JSONObject json = JSONObject.fromObject(html.getContent().replace("jsonp_info(", "").replace(");", "")
					.replace("$callback(", ""));
			JSONObject quantity = json.getJSONObject("quantity");
			sales = quantity.getString("quanity");

		} else {
			return;
		}

		// System.out.println(sales);
		ebd.setTransation(sales);

	}

	@Override
	public void parseModel(EbusinessData ebd, Node dom, Component component, String... args) {

		NodeList nl = commonList(component.getXpath(), dom);
		String params = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				params = nl.item(0).getTextContent();
				params = StringUtil.format(params);
				if (params.contains(":"))
					params = params.substring(params.indexOf(":") + 1);
				else if (params.contains("："))
					params = params.substring(params.indexOf("：") + 1);
			}
		}
		if (params.equals("") && ebd.getParams_params() != null)
			if (ebd.getParams_params().contains("型号: ")) {
				params = ebd.getParams_params();
				params = StringUtil.regMatcher(params, "型号: ", "[颜轮规]");
				if (params == null)
					return;
				if (params.contains(":"))
					params = params.replace(":", "");
				if (params.contains("："))
					params = params.replace("：", "");
			}
		ebd.setParams_model(StringUtil.format(params));
	}

	@Override
	public void parseWidth(EbusinessData ebd, Node dom, Component component, String... args) {

		NodeList nl = commonList(component.getXpath(), dom);
		String params = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				params = nl.item(0).getTextContent();
				params = StringUtil.format(params);
				if (params.contains(":"))
					params = params.substring(params.indexOf(":") + 1);
				else if (params.contains("："))
					params = params.substring(params.indexOf("：") + 1);
			}
		}
		if (params.equals("") && ebd.getParams_params() != null) {

			if (ebd.getParams_params().contains("宽度: ")) {
				params = ebd.getParams_params();
				params = StringUtil.regMatcher(params, "宽度: ", "[颜轮规mmMM]");
				if (params == null)
					return;
				if (params.contains(":"))
					params = params.replace(":", "");
				if (params.contains("："))
					params = params.replace("：", "");
			}
		}
		// if (params.equals("") && ebd.getParams_params() != null) {
		//
		// if (ebd.getParams_params().contains("宽度")
		// && (ebd.getParams_params().contains("mm") ||
		// ebd.getParams_params().contains("MM"))) {
		//
		// params = ebd.getParams_params();
		// params = params.toUpperCase();
		// params = StringUtil.regMatcher(params, "宽度", "MM");
		// if (params.contains(":"))
		// params = params.replace(":", "");
		// if (params.contains("："))
		// params = params.replace("：", "");
		// params += "MM";
		// }
		// }
		ebd.setParams_width(StringUtil.format(params).replace("mm", "").replace("MM", ""));
		Systemconfig.sysLog.log("width:" + params);
	}

	@Override
	public void parseDiameter(EbusinessData ebd, Node dom, Component component, String... args) {

		NodeList nl = commonList(component.getXpath(), dom);
		String params = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				params = nl.item(0).getTextContent();
				params = StringUtil.format(params);
				if (params.contains(":"))
					params = params.substring(params.indexOf(":") + 1);
				else if (params.contains("："))
					params = params.substring(params.indexOf("：") + 1);
			}
		}
		// if (params.equals("") && ebd.getParams_params() != null) {
		//
		// if (ebd.getParams_params().contains("直径") &&
		// ebd.getParams_params().contains("英寸")) {
		// params = ebd.getParams_params();
		// params = StringUtil.regMatcher(params, "直径", "英寸");
		// if (params.contains(":"))
		// params = params.replace(":", "");
		// if (params.contains("："))
		// params = params.replace("：", "");
		// params += "英寸";
		// }
		// }
		if (params.equals("") && ebd.getParams_params() != null) {

			if (ebd.getParams_params().contains("直径: ")) {
				params = ebd.getParams_params();
				params = StringUtil.regMatcher(params, "直径: ", "[颜轮规英寸]");
				if (params == null)
					return;
				if (params.contains(":"))
					params = params.replace(":", "");
				if (params.contains("："))
					params = params.replace("：", "");
			}
		}
		ebd.setParams_diameter(StringUtil.format(params).replace("英寸", ""));
		Systemconfig.sysLog.log("diameter:" + params);
	}

	@Override
	public void parseInfo_code(EbusinessData ebd, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub
		String oriUrl = ebd.getUrl();

		String info_code = oriUrl.substring(oriUrl.indexOf("id=") + 3);
		if (info_code.contains("&"))
			info_code = info_code.substring(0, info_code.indexOf("&"));

		ebd.setInfo_code(info_code);

	}

	@Override
	public String getOwnerInitUrl(String productId) {

		return null;
	}

	/**
	 * @param flag
	 *            0:jd, 1:tmall, 2:taobao
	 * @return
	 */
	@Override
	public String getCommentInitUrl(EbusinessData data) {
		if (data.getInfo_code() == null || data.getInfo_code().equals("")) {
			System.out.println("商品id为空，无法获取评论初始url");
			return null;
		}
		if (data.getOwner() == null) {
			System.err.println("当前商品卖家信息没有采集到,无法获取评论初始url");
			return null;
		}

		if (data.getOwner().getOwner_code() == null || data.getOwner().getOwner_code().equals("")) {
			System.out.println("卖家id为空，无法获取评论初始url");
			return null;
		}
		String url = data.getUrl();

		int flag = url.contains("tmall.com") ? 1 : 0;
		if (flag == 1)
			url = "http://rate.tmall.com/list_detail_rate.htm?itemId=" + data.getInfo_code() + "&currentPage=1";
		else {
			url = "http://rate.taobao.com/feedRateList.htm?userNumId=" + data.getOwner().getOwner_code()
					+ "&auctionNumId=" + data.getInfo_code() + "&currentPageNum=1&showContent=1";
		}

		return url;
	}

	@Override
	public String getCommentNext(String currUrl) {

		String strCurrPage = "";
		if (currUrl.contains("tmall"))
			strCurrPage = currUrl.substring(currUrl.lastIndexOf("=") + 1);
		else {
			strCurrPage = currUrl.substring(currUrl.lastIndexOf("PageNum=") + 8, currUrl.lastIndexOf("&"));
		}

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
		String url = "";
		if (currUrl.contains("tmall"))
			url = currUrl.substring(0, currUrl.lastIndexOf("=")) + "=" + intNext;
		else {
			url = currUrl.replace("currentPageNum=" + strCurrPage, "currentPageNum=" + intNext);
		}
		return url;
	}

	/*
	 * @param ed *
	 * 
	 * @param jsonContent json文件的内容
	 * 
	 * @param commentPage 当前页码
	 * 
	 * @return 评论下一页url
	 */
	public String templateCommentPage(EbusinessData ed, int commentPage, String... args) {
		String url = args[0];

		JSONArray jArray = null;

		String content = shp.getJsonContent(url, "gb2312");

		if (content == null) {
			Systemconfig.sysLog.log("没有抓取到评论页内容：" + url);
			return null;
		}
		int commentCount = 0;// 评论总数
		int pageCount = 0;// 最大评论页数
		int site = ed.getUrl().contains("tmall") ? 0 : 1;

		try {

			if (site == 0) {// tmall
				commentCount = Integer.parseInt(JsonUtil.getStringByKey(JsonUtil.getStringByKey(content, "rateCount"),
						"total"));
				if (commentCount == 0)
					return null;

				pageCount = commentCount / 20 + 1;

				JSONObject jObject = JSONObject.fromObject(content);

				jArray = jObject.getJSONArray("rateList");
				if (jArray.size() == 0) {
					Systemconfig.sysLog.log("未能获得评论列表.");
					return null;
				}

				Systemconfig.sysLog.log("获得评论列表.");
			} else if (site == 1) {// taobao
				String strpageCount = JsonUtil.getStringByKey(content, "maxPage");

				if (strpageCount.equals("0") || strpageCount.equals("") || strpageCount == null)
					return null;
				pageCount = Integer.parseInt(strpageCount);
				//
				JSONObject jObject = JSONObject.fromObject(content);
				try {
					jArray = jObject.getJSONArray("comments");
				} catch (Exception e1) {
					System.out.println("以达到淘宝产品评论最后页");
					return null;
				}
				if (jArray.size() == 0) {
					Systemconfig.sysLog.log("未能获得评论列表.");
					return null;
				}

				Systemconfig.sysLog.log("获得评论列表.");

			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		for (int i = 0; i < jArray.size(); i++) { // 一页中的每一条评论
			try {

				Systemconfig.sysLog.log("获取第:" + (10 * (commentPage - 1) + i) + "条评论...(评论总数:" + commentCount + ").");
				JSONObject comment = jArray.getJSONObject(i);

				String id = "";
				String person = "";
				String level = "";
				String info = "";
				String pubtime = "";
				String label = "";
				String product = "";
				String score = "";

				if (site == 0) {// tmall
					id = comment.getString("id");
					person = comment.getString("displayUserNick");
					level = comment.getString("tamllSweetLevel");

					info = comment.getString("rateContent");
					pubtime = comment.getString("rateDate");
					label = "";

					product = comment.getString("auctionSku");// 型号
					score = "";// 淘宝天猫貌似不公开评论分数
				} else if (site == 1) {// taobao
					JSONObject user = comment.getJSONObject("user");
					JSONObject auction = comment.getJSONObject("auction");

					person = user.getString("nick");
					level = user.getString("vipLevel");
					info = comment.getString("content");
					pubtime = comment.getString("date");

					product = auction.getString("sku");
					score = comment.getString("rate");
					id = comment.getString("rateId");
				} else {

				}
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
				e.printStackTrace();
			}

		}// end for

		String nextUrl = getCommentNext(url);
		// if (commentPage > pageCount)
		// return null;
		return nextUrl;
	}

	// public void templateOwnerPage(EbusinessData data, HtmlInfo html, int
	// page, String... keyword) {
	// Siteinfo siteinfo = Systemconfig.allSiteinfos.get(html.getSite());
	// // create(content);
	// Node domtree = getRealDOM(html);
	// if (domtree == null) {
	// Systemconfig.sysLog.log("DOM解析为NULL！！");
	// return;
	// }
	// CommonComponent comp = getRealComp(siteinfo,
	// CollectDataType.DATA.name());// 得到元数据的配置组件
	// OwnerData od = new OwnerData();
	// od.setOwner_product(data.getInfo_code());
	// data.setOwner(od);
	//
	// this.parseOwner_code(data, domtree, comp.getComponents().get(""),
	// html.getContent());
	// this.parseOwner_address(data, domtree,
	// comp.getComponents().get("owner_address"), html.getContent());
	// this.parseOwner_company(data, domtree,
	// comp.getComponents().get("owner_company"), html.getContent());
	// this.parseOwner_name(data, domtree,
	// comp.getComponents().get("owner_name"), html.getContent());
	// this.parseOwner_pScore(data, domtree,
	// comp.getComponents().get("owner_pscore"), html.getContent());
	// this.parseOwner_score(data, domtree,
	// comp.getComponents().get("owner_score"), html.getContent());
	// this.parseOwner_sScore(data, domtree,
	// comp.getComponents().get("owner_sscore"), html.getContent());
	// this.parseOwner_url(data, domtree, comp.getComponents().get("owner_url"),
	// html.getContent());
	//
	// }

	/**
	 * @param od
	 *            卖家对象
	 * @param jsonContent
	 *            内容页源码字符串
	 */
	@Override
	public void parseOwner_address(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);

		String address = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				address = nl.item(0).getTextContent();
				address = StringUtil.format(address);
			}
		}
		if (ebd.getOwner() != null)
			ebd.getOwner().setOwner_address(address);
		else {
			OwnerData od = new OwnerData();
			od.setOwner_address(address);
			ebd.setOwner(od);
		}
	}

	@Override
	public void parseOwner_code(EbusinessData ebd, Node dom, Component component, String... args) {
		String content = args[0];
		String regex = "userId=\\d+&";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			String tmp = matcher.group();
			String ownerCode = tmp.replace("userId=", "").replace("&", "");
			if (ebd.getOwner() != null)
				ebd.getOwner().setOwner_code(ownerCode);
			else {
				OwnerData od = new OwnerData();
				od.setOwner_code(ownerCode);
				ebd.setOwner(od);
			}
		} else {
			regex = "\"userId\":\"\\d+\",\",";
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(content);
			if (matcher.find()) {
				String tmp = matcher.group();
				String ownerCode = tmp.replace("\"userId\":\"", "").replace("\",\"", "");
				if (ebd.getOwner() != null)
					ebd.getOwner().setOwner_code(ownerCode);
				else {
					OwnerData od = new OwnerData();
					od.setOwner_code(ownerCode);
					ebd.setOwner(od);
				}
			} else {
				regex = "userid=\\d+;";
				;
				pattern = Pattern.compile(regex);
				matcher = pattern.matcher(content);
				if (matcher.find()) {
					String tmp = matcher.group();
					String ownerCode = tmp.replace("userid=", "").replace(";", "");
					if (ebd.getOwner() != null)
						ebd.getOwner().setOwner_code(ownerCode);
					else {
						OwnerData od = new OwnerData();
						od.setOwner_code(ownerCode);
						ebd.setOwner(od);
					}
				}
			}
		}

	}

	@Override
	public void parseOwner_company(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String result = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				result = nl.item(0).getTextContent();
				result = StringUtil.format(result);
			}
		}
		if (ebd.getOwner() != null)
			ebd.getOwner().setOwner_company(result);
		else {
			OwnerData od = new OwnerData();
			od.setOwner_company(result);
			ebd.setOwner(od);
		}
	}

	@Override
	public void parseOwner_name(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String result = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				result = nl.item(0).getTextContent();
				result = StringUtil.format(result);
			}
		}
		if (ebd.getOwner() != null)
			ebd.getOwner().setOwner_name(result);
		else {
			OwnerData od = new OwnerData();
			od.setOwner_name(result);
			ebd.setOwner(od);
		}
	}

	@Override
	public void parseOwner_pScore(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String result = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				result = nl.item(0).getTextContent();
				result = StringUtil.format(result);
			}
		}
		if (ebd.getOwner() != null)
			ebd.getOwner().setOwner_pScore(result);
		else {
			OwnerData od = new OwnerData();
			od.setOwner_pScore(result);
			ebd.setOwner(od);
		}
	}

	@Override
	public void parseOwner_score(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String result = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				result = nl.item(0).getTextContent();
				result = StringUtil.format(result);
			}
		}

		if (ebd.getOwner() != null)
			ebd.getOwner().setOwner_score(result);
		else {
			OwnerData od = new OwnerData();
			od.setOwner_score(result);
			ebd.setOwner(od);
		}
	};

	@Override
	public void parseOwner_sScore(EbusinessData ebd, Node dom, Component component, String... args) {
		NodeList nl = commonList(component.getXpath(), dom);
		String result = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				result = nl.item(0).getTextContent();
				result = StringUtil.format(result);
			}
		}

		if (ebd.getOwner() != null)
			ebd.getOwner().setOwner_sScore(result);
		else {
			OwnerData od = new OwnerData();
			od.setOwner_sScore(result);
			ebd.setOwner(od);
		}
	}

	@Override
	public String parseOwner_url(EbusinessData ebd, Node dom, Component component, String... args) {

		NodeList nl = commonList(component.getXpath(), dom);
		String result = "";
		if (nl != null) {
			if (nl.item(0) != null) {
				result = nl.item(0).getTextContent();
				result = StringUtil.format(result);
			}
		}

		if (ebd.getOwner() != null)
			ebd.getOwner().setOwner_url(result);
		else {
			OwnerData od = new OwnerData();
			od.setOwner_url(result);
			ebd.setOwner(od);
		}
		ebd.getOwner().setOwner_url(result);
		return result;
	}

}
