package common.down.weibo;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import common.bean.HtmlInfo;
import common.bean.WeixinData;
import common.down.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;

/**
 * 下载详细页面
 * @author grs
 */
public class WeiboSearchDataCommonDownload extends GenericDataCommonDownload<WeixinData> implements Runnable {

	public WeiboSearchDataCommonDownload(String siteFlag, WeixinData data, CountDownLatch endCount,SearchKey key) {
		super(siteFlag, data, endCount, key);
	}
	
	public void process() {
		String url = getRealUrl(data);
		if(url==null) return;
		//检测是否需要代理，未来版本改进
		siteinfo.setAgent(false);
		HtmlInfo html = htmlInfo("DATA");
		
		try {
			if (url != null && !url.equals("")) {
				html.setOrignUrl(url);
				
				http.getContent(html);
//				html.setContent();
				if(html.getContent()==null) {
					return;
				}
				//解析数据
				xpath.templateContentPage(data, html);
				
				Systemconfig.sysLog.log(data.getTitle() + "解析完成。。。");
				Systemconfig.dbService.saveData(data);
				Systemconfig.sysLog.log(data.getTitle() + "保存完成。。。");
			}
//			if(data.getSameUrl()!=null && count != null && data.getId()>0) {
//				//采集链接
//				SearchKey searchKey = new SearchKey();
//				searchKey.setKey(data.getSameUrl());
//				searchKey.setId(data.getId());
//				searchKey.setSite(siteFlag);
//				TimeUtil.rest(siteinfo.getDownInterval()-10);
//				new NewsMetaCommonDownload(searchKey).process();
//			}
		} catch (Exception e) {
			Systemconfig.sysLog.log("采集出现异常"+url, e);
			try {
				Systemconfig.dbService.saveLog(siteFlag, key, 3, url+"\r\n"+ e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			if(count != null)
				count.countDown();
		}
	}
	
}
