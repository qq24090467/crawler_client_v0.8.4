package common.down.weibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.bean.HtmlInfo;
import common.bean.UserData;
import common.down.GenericDataCommonDownload;
import common.down.GenericMetaCommonDownload;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.weibo.monitor.WeiboMonitorXpathExtractor;
import common.rmi.packet.SearchKey;
import common.siteinfo.CollectDataType;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.util.TimeUtil;

/**
 * 下载数据
 * @author grs
 */
public class FansCommonDownload extends GenericMetaCommonDownload<UserData> {
	
	private UserAttr user;
	private int id;
	public FansCommonDownload(SearchKey key, int id, UserAttr user) {
		super(key);
		this.id = id;
		this.user = user;
	}
	@Override
	public void process() {
		List<UserData> alllist = new ArrayList<UserData>();
		List<UserData> list = new ArrayList<UserData>();
		String url = getRealUrl(siteinfo, gloaburl);
		String nexturl = url;
		HtmlInfo html = htmlInfo(CollectDataType.FANS.name());
		int count = 1;
		try {
			while(nexturl != null && !nexturl.equals("")) {
				list.clear();
				
				html.setOrignUrl(nexturl);
				
				try {
					http.getContent(html, user);
//					html.setContent(common.util.StringUtil.getContent("filedown/FANS/sina/50b7702c4c3dc15a1cf1c56155b08d46.htm"));
					
					nexturl = ((WeiboMonitorXpathExtractor)((XpathExtractor)xpath)).templateRelation(list, html, count, id+"", nexturl);
					
					if(list.size()==0) {
						Systemconfig.sysLog.log(url + "元数据页面解析为空！！");
						break;
					}
					Systemconfig.sysLog.log(url + "元数据页面解析完成。");
					
					Systemconfig.dbService.getNorepeatData(list, "");
					
					alllist.addAll(list);
					
					url = nexturl;
					count++;
					if(nexturl!=null) 
						TimeUtil.rest(siteinfo.getDownInterval());
					
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Systemconfig.dbService.saveLog(siteFlag, key, 3, url+"\r\n"+ e.getMessage());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
			Systemconfig.dbService.saveDatas(alllist);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			alllist.clear();
			list.clear();
		}
	}
	
}
