package common.down;

import java.util.concurrent.CountDownLatch;

import common.bean.CommonData;
import common.bean.HtmlInfo;
import common.down.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;

/**
 * 下载详细页面
 * @author grs
 */
public class SimpleDataCommonDownload extends GenericDataCommonDownload<CommonData> implements Runnable {

	public SimpleDataCommonDownload(String siteFlag, CommonData data, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, data, endCount, key);
	}
	
	public void process() {
		String url = getRealUrl(data);
		if(url==null) return;
		HtmlInfo html = htmlInfo("DATA");
		try {
			if (url != null && !url.equals("")) {
				html.setOrignUrl(url);
				
				http.getContent(html);
				if(html.getContent()==null) {
					return;
				}
				//解析数据
				xpath.templateContentPage(data, html);
				
				Systemconfig.sysLog.log(data.getTitle() + "解析完成。。。");
				Systemconfig.dbService.saveData(data);
				Systemconfig.sysLog.log(data.getTitle() + "保存完成。。。");
			}
		} catch (Exception e) {
			Systemconfig.sysLog.log("采集出现异常"+url, e);
		} finally {
			if(count != null)
				count.countDown();
		}
	}
	
}
