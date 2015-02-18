package common.down.ebusiness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import common.bean.CommentData;
import common.bean.EbusinessData;
import common.bean.HtmlInfo;
import common.down.GenericDataCommonDownload;
import common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.TimeUtil;

public class EbusinessCommentDownload extends GenericDataCommonDownload<EbusinessData> {

	public EbusinessCommentDownload(String siteFlag, EbusinessData vd, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	@Override
	public void process() {
		String url="";
		try {
			int commentPage = 0;
			Systemconfig.sysLog.log("--------------------------------评论抽取" + data.getUrl()
					+ "...----------------------------");
			// 获取评论第一页的url

			 url= ((EbusinessSearchXpathExtractor) xpath).getCommentInitUrl(data);
			while (true) {

				Systemconfig.sysLog.log(">>>正采集评论第" + commentPage + "页...");

				// 评论信息抽取，得到下一页url
				String nextUrl = ((EbusinessSearchXpathExtractor) xpath).templateCommentPage(data, commentPage, url);//

				setCommentMd5(data);

				Systemconfig.sysLog.log("-------------评论数据第" + commentPage + "页解析完成。----------" + url);
				url = nextUrl;

				commentPage++;
				if (commentPage > 10)// 暂时采前5页评论
					break;

				if (url == null)// 没有评论or已采集最后一页
					break;
				TimeUtil.rest(5);// 间隔5秒

			}// end while 评论所有页采集完成
			Systemconfig.sysLog.log("-----------------------------" + data.getTitle()
					+ " 所有评论采集完成。------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Systemconfig.dbService.saveLog(siteFlag, key, 3, url+"\r\n"+e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	protected void specialHtmlInfo(HtmlInfo html) {
		if (html.getSite().equals("_search")) {
			html.setCookie("BAIDUID=AE6A9BCA58B8CEBCF4AC160505049A93:FG=1; favoriteTips=ok; Hm_lvt_e9e114d958ea263de46e080563e254c4=1409106616,1409540228,1409725298,1409800157; BDUSS=Gs1Z3B1NklIU0xSYXZwdGQ5QnlyamlseldVcVYxS0VwRGoxZDFFNlJLRDFycGRUQVFBQUFBJCQAAAAAAAAAAAEAAAD9k1QKd3NncnMwMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPUhcFP1IXBTV; LOCALGX=%u5317%u4EAC%7C%30%7C%u5317%u4EAC%7C%30; BDNVCODE=5407D7C4D634B4056646753; Hm_lpvt_e9e114d958ea263de46e080563e254c4=1409800157; BAIDUID=AE6A9BCA58B8CEBCF4AC160505049A93:FG=1; BD_CK_SAM=1; BDSVRTM=258; H_PS_PSSID=");
		}
	}

	private static void setCommentMd5(EbusinessData data) {
		if (data.getComments() == null)
			return;
		ArrayList<CommentData> list = (ArrayList<CommentData>) data.getComments();
		for (CommentData commentData : list) {
			commentData.setMd5(MD5Util.MD5(commentData.getComment_product() + commentData.getComment_id()
					+ data.getInfo_code()));
		}
	}
}
