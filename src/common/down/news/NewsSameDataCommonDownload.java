package common.down.news;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import common.bean.HtmlInfo;
import common.bean.NewsData;
import common.down.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.service.mysql.NewsMysqlService;
import common.service.oracle.NewsOracleService;
import common.system.Systemconfig;
import common.util.TimeUtil;

/**
 * 下载详细页面
 * @author grs
 */
public class NewsSameDataCommonDownload extends GenericDataCommonDownload<NewsData> implements Runnable {

	public NewsSameDataCommonDownload(String siteFlag, NewsData vd,
			CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	@Override
	public void process() {
		TimeUtil.rest(siteinfo.getDownInterval());
		String url = getRealUrl(data);
		if(url==null) return;
		HtmlInfo html = htmlInfo("SAME");
		try {
			if (url != null && !url.equals("")) {
				html.setOrignUrl(url);
				
				http.getContent(html);
				if(html.getContent()==null) {
					return;
				}
				//解析数据
				xpath.templateContentPage(data, html);
				Systemconfig.sysLog.log("相同新闻"+data.getTitle() + "解析完成。。。");
				if(Systemconfig.dbService instanceof NewsOracleService)
					((NewsOracleService)Systemconfig.dbService).saveSameData(data);
				else if(Systemconfig.dbService instanceof NewsMysqlService)
					((NewsMysqlService)Systemconfig.dbService).saveSameData(data);
				Systemconfig.sysLog.log("相同新闻"+data.getTitle() + "保存完成。。。");
			}
		} catch (Exception e) {
			Systemconfig.sysLog.log("采集出现异常"+url, e);
			try {
				Systemconfig.dbService.saveLog(siteFlag, key, 3, url+"\r\n"+ e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} 
	}
	
}
