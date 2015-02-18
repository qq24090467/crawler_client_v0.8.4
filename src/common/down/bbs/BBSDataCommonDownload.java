package common.down.bbs;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import common.bean.BBSData;
import common.bean.HtmlInfo;
import common.down.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.service.mysql.BbsMysqlService;
import common.service.oracle.BbsOracleService;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.system.UserManager;
import common.util.StringUtil;

/**
 * 下载详细页面
 * 
 * @author grs
 */
public class BBSDataCommonDownload extends GenericDataCommonDownload<BBSData> {

	public BBSDataCommonDownload(String siteFlag, BBSData vd, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	@Override
	public void process() {
		String url = getRealUrl(data);
		if (siteFlag.startsWith("tieba")) {
			// if(!url.contains("pid")) return;
		}
		if (url == null)
			return;
		HtmlInfo html = htmlInfo("DATA");
		try {
			if (url != null && !url.equals("")) {
				html.setOrignUrl(url);
				http.getContent(html);
				// html.setContent(StringUtil.getContent("E:/grs/开源工具/crawler(_client)_0.8.1/filedown/DATA/tieba_bbs_search/dcb64a74de7c2a750f5e5cfcf0d20697.htm",
				// siteinfo.getCharset()));
				if (html.getContent() == null||(html.getContent().contains("抱歉，您访问的贴子被隐藏")&&html.getContent().contains("贴吧404"))) {
					return;
				}
				// 解析数据
				url = xpath.templateContentPage(data, html);

				Systemconfig.sysLog.log(data.getTitle() + "解析完成。。。");
				Systemconfig.dbService.saveData(data);
				synchronized (key) {
					key.savedCountIncrease();
				}
				Systemconfig.sysLog.log(data.getTitle() + "保存完成。。。");
			}
			/** 2015-1-27 评论单独程序采集 gxd */
			// String lastUrl = "";
			// int page = 2;
			// while (url != null) {
			// if (url.equals(lastUrl))
			// break;
			// lastUrl = url;
			// data.getReplyList().clear();
			// html.setType("REPLY" + File.separator + siteFlag);
			// if (siteFlag.contains("xcar") || siteFlag.contains("autohome"))//
			// 试试
			// html.setType("DATA" + File.separator + siteFlag);
			// html.setOrignUrl(url);
			// http.getContent(html);
			// url = xpath.templateContentPage(data, html, page);
			// Systemconfig.dbService.getNorepeatData(data.getReplyList(), "");
			// Systemconfig.sysLog.log("\t" + data.getTitle() + " 评论  第" + (page
			// - 1) + "页 解析完成。。。");
			// if (Systemconfig.dbService instanceof BbsOracleService) {
			// ((BbsOracleService)
			// (Systemconfig.dbService)).saveCommonData(data);
			// synchronized (key) {
			// key.savedCountIncrease();
			// }
			// }
			//
			// else if (Systemconfig.dbService instanceof BbsMysqlService)
			// ((BbsMysqlService)
			// (Systemconfig.dbService)).saveCommonData(data);
			// Systemconfig.sysLog.log("\t" + data.getTitle() + " 评论  第" + (page
			// - 1) + "页 保存完成。。。");
			// page++;
			// }
		} catch (Exception e) {
			try {
				Systemconfig.dbService.saveLog(siteFlag, key, 3, data.getUrl() + "\r\n" + e);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Systemconfig.sysLog.log("采集出现异常" + data.getUrl(), e);
		} finally {
			if (count != null)
				count.countDown();
		}
	}

}
