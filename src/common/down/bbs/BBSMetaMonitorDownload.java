package common.down.bbs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import common.bean.BBSData;
import common.bean.HtmlInfo;
import common.down.DataThreadControl;
import common.down.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.system.UserManager;
import common.util.TimeUtil;

/**
 * 下载元数据
 * @author grs
 */
public class BBSMetaMonitorDownload extends GenericMetaCommonDownload<BBSData> implements Runnable {
	public BBSMetaMonitorDownload(SearchKey key) {
		super(key);
	}
	
	public void process() {
		UserAttr ua = UserManager.getUser(siteFlag);
		UserManager.releaseUser(siteFlag, ua);
		List<BBSData> alllist = new ArrayList<BBSData>();
		List<BBSData> list = new ArrayList<BBSData>();
		String url = getRealUrl(siteinfo, gloaburl);
		int page = getRealPage(siteinfo);
		String keyword = key.getKey();
		map.put(keyword, 1);
		String nexturl = url;
		DataThreadControl dtc = new DataThreadControl(siteFlag, keyword);
		HtmlInfo html = htmlInfo("META");
		int totalCount = 0;
		while(nexturl != null && !nexturl.equals("")) {
			list.clear();
			
			html.setOrignUrl(nexturl);
			
			try {
				http.getContent(html, ua);
//					html.setContent(common.util.StringUtil.getContent("filedown/META/baidu/37b30f2108ed06501ad6a769ca8cedc8.htm"));
				
				nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole()+"");
				
				if(list.size()==0) {
					Systemconfig.sysLog.log(url + "元数据页面解析为空！！");
					TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				Systemconfig.sysLog.log(url + "元数据页面解析完成。");
				totalCount += list.size();
				Systemconfig.dbService.getNorepeatData(list, "");
				if(list.size()==0) {
					TimeUtil.rest(siteinfo.getDownInterval());
//					break;
				}
				alllist.addAll(list);
				
				map.put(keyword, map.get(keyword)+1);
				if(map.get(keyword)>page) break;
				url = nexturl;
				if(nexturl!=null) 
					TimeUtil.rest(siteinfo.getDownInterval());
				
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Systemconfig.dbService.saveLog(siteFlag, key, 3, nexturl+"\r\n"+e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}
		}
		try {
			Systemconfig.dbService.saveLog(siteFlag, key, 2, totalCount + "", alllist.size() + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		dtc.process(alllist, siteinfo.getDownInterval(),key);
	}
	
}
