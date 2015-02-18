package common.down;

import java.util.concurrent.CountDownLatch;

import common.bean.BBSData;
import common.bean.BlogData;
import common.bean.CommonData;
import common.bean.EbusinessData;
import common.bean.NewsData;
import common.bean.ReportData;
import common.bean.VideoData;
import common.bean.WeixinData;
import common.down.bbs.BBSDataCommonDownload;
import common.down.bbs.BBSDataMonitorDownload;
import common.down.bbs.BBSMetaCommonDownload;
import common.down.bbs.BBSMetaMonitorDownload;
import common.down.blog.BlogDataCommonDownload;
import common.down.blog.BlogMetaCommonDownload;
import common.down.ebusiness.EbusinessDataCommonDownload;
import common.down.ebusiness.EbusinessMetaCommonDownload;
import common.down.news.NewsDataCommonDownload;
import common.down.news.NewsMetaCommonDownload;
import common.down.news.NewsMonitorMetaCommonDownload;
import common.down.report.ReportDataCommonDownload;
import common.down.report.ReportMetaCommonDownload;
import common.down.video.VideoDataCommonDownload;
import common.down.video.VideoMetaCommonDownload;
import common.down.weibo.WeiboMonitorMetaCommonDownload;
import common.down.weibo.WeiboSearchMetaCommonDownload;
import common.down.weixin.WeixinDataCommonDownload;
import common.down.weixin.WeixinMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;

/**
 * 下载线程的工厂方法 可处理特殊的站点下载
 * 
 * @author grs
 * 
 */
public class DownFactory {
	/**
	 * 元数据
	 * 
	 * @param siteFlag
	 * @param type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static GenericMetaCommonDownload metaControl(SearchKey key) {
		switch (Systemconfig.crawlerType) {
		case 1:
			return new NewsMetaCommonDownload(key);
		case 2:
			return new NewsMonitorMetaCommonDownload(key);
		case 3:
			return new BBSMetaCommonDownload(key);
		case 4:
			return new BBSMetaMonitorDownload(key);
		case 5:
		case 6:
			return new BlogMetaCommonDownload(key);
		case 7:
			return new WeiboSearchMetaCommonDownload(key);
		case 8:
			return new WeiboMonitorMetaCommonDownload(key);
		case 9:
		case 10:
			return new VideoMetaCommonDownload(key);
			// case 11 :
			// case 12 : return new AcademicMetaCommonDownload(key);
		case 13:
		case 14:
			return new EbusinessMetaCommonDownload(key);
		case 15:
		case 16:
			return new WeixinMetaCommonDownload(key);
		case 21:
		case 22:
			return new ReportMetaCommonDownload(key);

		default:
			return new SimpleMetaCommonDownload(key);
		}
	}

	/**
	 * 详细数据
	 * 
	 * @param siteFlag
	 * @param type
	 * @return
	 */
	public static <T> GenericDataCommonDownload<T> dataControl(String siteFlag, T data, CountDownLatch count,
			SearchKey key) {
		switch (Systemconfig.crawlerType) {
		case 1:
		case 2:
			return (GenericDataCommonDownload<T>) new NewsDataCommonDownload(siteFlag, (NewsData) data, count, key);
		case 3:
			return (GenericDataCommonDownload<T>) new BBSDataCommonDownload(siteFlag, (BBSData) data, count, key);
		case 4:
			return (GenericDataCommonDownload<T>) new BBSDataMonitorDownload(siteFlag, (BBSData) data, count, key);
		case 5:
		case 6:
			return (GenericDataCommonDownload<T>) new BlogDataCommonDownload(siteFlag, (BlogData) data, count, key);
		case 7:
		case 8:
			return null;// (GenericDataCommonDownload<T>) new
						// WeiboDataCommonDownload(siteFlag, (WeiboData) data,
						// count, null);
		case 9:
		case 10:
			return (GenericDataCommonDownload<T>) new VideoDataCommonDownload(siteFlag, (VideoData) data, count, key);
			// case 11 :
			// case 12 : return (GenericDataCommonDownload<T>) new
			// AcademicDataCommonDownload(siteFlag, (Data) data, count);
		case 13:
		case 14:
			return (GenericDataCommonDownload<T>) new EbusinessDataCommonDownload(siteFlag, (EbusinessData) data, count, key);
		case 15:
		case 16:
			return (GenericDataCommonDownload<T>) new WeixinDataCommonDownload(siteFlag, (WeixinData) data, count, key);

		case 21:
		case 22:
			return (GenericDataCommonDownload<T>) new ReportDataCommonDownload(siteFlag, (ReportData) data, count, key);

		default:
			return (GenericDataCommonDownload<T>) new SimpleDataCommonDownload(siteFlag, (CommonData) data, count, key);
		}
	}

}
