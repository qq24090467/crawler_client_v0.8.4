package common.down;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import common.bean.CommonData;
import common.bean.HtmlInfo;
import common.extractor.Extractor;
import common.extractor.xpath.SimpleXpathExtractor;
import common.extractor.xpath.academic.monitor.AcademicMonitorXpathExtractor;
import common.extractor.xpath.bbs.monitor.BbsMonitorXpathExtractor;
import common.extractor.xpath.bbs.search.BbsSearchXpathExtractor;
import common.extractor.xpath.blog.monitor.BlogMonitorXpathExtractor;
import common.extractor.xpath.blog.search.BlogSearchXpathExtractor;
import common.extractor.xpath.ebusiness.monitor.EbusinessMonitorXpathExtractor;
import common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor;
import common.extractor.xpath.news.monitor.NewsMonitorXpathExtractor;
import common.extractor.xpath.news.search.NewsSearchXpathExtractor;
import common.extractor.xpath.video.monitor.VideoMonitorXpathExtractor;
import common.extractor.xpath.video.search.VideoSearchXpathExtractor;
import common.extractor.xpath.weibo.monitor.WeiboMonitorXpathExtractor;
import common.extractor.xpath.weibo.search.WeiboSearchXpathExtractor;
import common.extractor.xpath.weixin.monitor.WeixinMonitorXpathExtractor;
import common.extractor.xpath.weixin.search.WeixinSearchXpathExtractor;
import common.http.HttpProcess;
import common.http.SimpleHttpProcess;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.EncoderUtil;

/**
 * 数据下载父类
 * 
 * @author grs
 * 
 */
public abstract class GenericCommonDownload<T> {

	protected static final Map<String, Extractor> xpathMap = new HashMap<String, Extractor>();
	protected T data;
	protected CountDownLatch count;
	protected final String siteFlag;
	protected final SearchKey key;
	protected final String gloaburl;
	protected final Siteinfo siteinfo;
	protected HttpProcess http;

	protected Extractor<T> xpath;
	private Throwable exception;

	public void throwException() throws IOException {
		if (exception instanceof IOException)
			throw (IOException) exception;
	}

	public GenericCommonDownload(String siteFlag, T data, CountDownLatch endCount, SearchKey key) {
		this.siteFlag = siteFlag;
		this.data = data;
		this.count = endCount;
		this.key = key;
		siteinfo = Systemconfig.allSiteinfos.get(siteFlag);
		// key = null;
		gloaburl = null;
		createHttpClient(siteFlag);
		if (xpathMap.containsKey(siteFlag)) {
			this.xpath = xpathMap.get(siteFlag);
		} else {
			this.xpath = getXpath();
			xpathMap.put(siteFlag, this.xpath);
		}
	}

	public GenericCommonDownload(SearchKey key) {
		this.siteFlag = key.getSite();
		this.key = key;

		siteinfo = Systemconfig.allSiteinfos.get(siteFlag);
		String url = siteinfo.getUrl();
		if (url != null && !url.startsWith("${") && url.contains("<keyword>"))
			url = url.replace("<keyword>", EncoderUtil.encodeKeyWords(key.getKey(), siteinfo.getCharset()));
		else
			url = key.getKey();
		gloaburl = url;
		createHttpClient(siteFlag);

		if (xpathMap.containsKey(siteFlag)) {
			this.xpath = xpathMap.get(siteFlag);
		} else {
			this.xpath = getXpath();
			xpathMap.put(siteFlag, this.xpath);
		}
	}

	public GenericCommonDownload(String siteFlag, String url) {
		this.siteFlag = siteFlag;
		this.key = null;
		this.gloaburl = url;
		siteinfo = Systemconfig.allSiteinfos.get(siteFlag);
		createHttpClient(siteFlag);

		if (xpathMap.containsKey(siteFlag)) {
			this.xpath = xpathMap.get(siteFlag);
		} else {
			this.xpath = getXpath();
			xpathMap.put(siteFlag, this.xpath);
		}
	}

	/**
	 * 对入口URL做处理
	 * 
	 * @return
	 */
	protected String getRealUrl(Siteinfo siteinfo, String url) {
		if (url == null)
			return siteinfo.getUrl();
		else
			return url;
	}

	/**
	 * 对入口URL做处理
	 * 
	 * @return
	 */
	protected String getRealUrl(CommonData data) {
		return data.getUrl();
	}

	protected int getRealPage(Siteinfo siteinfo) {
		return siteinfo.getPage();
	}

	protected HtmlInfo htmlInfo(String type) {
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(siteFlag);
		HtmlInfo html = new HtmlInfo();
		html.setEncode(siteinfo.getCharset());
		html.setSite(siteFlag);
		html.setAgent(siteinfo.getAgent());
		html.setType(type + File.separator + siteFlag);
		specialHtmlInfo(html);
		return html;
	}

	protected void specialHtmlInfo(HtmlInfo html) {
	}

	@SuppressWarnings("rawtypes")
	protected Extractor getXpath() {
		Extractor be = null;
		try {
			String cl = Systemconfig.siteExtractClass.get(siteFlag);
			if (cl != null)
				be = (Extractor) Class.forName(cl).newInstance();
			else {
				String name = CrawlerType.getMap().get(Systemconfig.crawlerType).name().toLowerCase();
				String key = "";
				int fg = name.indexOf("_");
				if (fg != -1) {
					key = name.substring(0, 1).toUpperCase() + name.substring(1, fg) + name.substring(fg + 1, fg + 2).toUpperCase()
							+ name.substring(fg + 2);
				} else
					key = name.substring(0, 1).toUpperCase() + name.substring(1);
				String s = "common.extractor.xpath." + name.substring(0, name.indexOf("_")) + "."
						+ (Systemconfig.crawlerType % 2 == 0 ? "monitor" : "search") + "." + key + "XpathExtractor";
				System.out.println(s);
				// 没有配置的xpath，根据爬虫类型判断使用哪种抽取器
				be = (Extractor) Class.forName(s).newInstance();
			}
		} catch (ClassNotFoundException e) {
			// "未找到注册类，更新后处理"
		} catch (InstantiationException e) {
			// 注册类实例化异常
		} catch (IllegalAccessException e) {
			// 非法访问注册类
		} catch (Exception e) {
		}
		if (be == null) {
			// 没有配置的xpath，根据爬虫类型判断使用哪种抽取器
			switch (Systemconfig.crawlerType) {
			case 1:
				be = new NewsSearchXpathExtractor();
				break;
			case 3:
				be = new BbsSearchXpathExtractor();
				break;
			case 5:
				be = new BlogSearchXpathExtractor();
				break;
			case 7:
				be = new WeiboSearchXpathExtractor();
				break;
			case 9:
				be = new VideoSearchXpathExtractor();
				break;
			case 11:
				be = new AcademicMonitorXpathExtractor();
				break;
			case 13:
				be = new EbusinessSearchXpathExtractor();
				break;
			case 15:
				be = new WeixinSearchXpathExtractor();
				break;

			case 2:
				be = new NewsMonitorXpathExtractor();
				break;
			case 4:
				be = new BbsMonitorXpathExtractor();
				break;
			case 6:
				be = new BlogMonitorXpathExtractor();
				break;
			case 8:
				be = new WeiboMonitorXpathExtractor();
				break;
			case 10:
				be = new VideoMonitorXpathExtractor();
				break;
			case 12:
				be = new AcademicMonitorXpathExtractor();
				break;
			case 14:
				be = new EbusinessMonitorXpathExtractor();
				break;
			case 16:
				be = new WeixinMonitorXpathExtractor();
				break;
			default:
				be = new SimpleXpathExtractor();
			}
		}
		return be;
	}

	private void createHttpClient(String siteFlag) {
		try {
			http = (HttpProcess) Class.forName(Systemconfig.siteHttpClass.get(siteFlag)).newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
		} catch (Exception e) {
		}
		if (http == null) {
			http = new SimpleHttpProcess();
		}
	}

}
