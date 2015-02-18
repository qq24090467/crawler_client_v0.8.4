package common.down.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.bean.HtmlInfo;
import common.bean.NewsData;
import common.down.DataThreadControl;
import common.down.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;

/**
 * 下载元数据
 * 
 * @author grs
 */
public class NewsMetaCommonDownload extends GenericMetaCommonDownload<NewsData> {

	public NewsMetaCommonDownload(SearchKey key) {
		super(key);
	}

	@Override
	public void process() {
		List<NewsData> alllist = new ArrayList<NewsData>();
		List<NewsData> list = new ArrayList<NewsData>();
		String url = getRealUrl(siteinfo, key.getId() > 0 ? key.getKey() : gloaburl);
		int page = getRealPage(siteinfo);
		String keyword = key.getKey();
		map.put(keyword, 1);
		String nexturl = url;
		DataThreadControl dtc = new DataThreadControl(siteFlag, keyword);
		HtmlInfo html = htmlInfo("META");
		int totalCount = 0;
		while (nexturl != null && !nexturl.equals("")) {
			list.clear();

			html.setOrignUrl(nexturl);
			try {
				http.getContent(html);
				// html.setContent(common.util.StringUtil.getContent("filedown/META/baidu_news_search/6f962c1b7d205db4faf80453362b648e.htm"));
				nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole() + "");

				if (list.size() == 0) {
					Systemconfig.sysLog.log(url + "元数据页面解析为空！！");
					TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				Systemconfig.sysLog.log(url + "元数据页面解析完成。");
				totalCount += list.size();
				Systemconfig.dbService.getNorepeatData(list, "");
				if (list.size() == 0) {
					if (alllist.size() == 0)
						TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				alllist.addAll(list);

				map.put(keyword, map.get(keyword) + 1);
				if (map.get(keyword) > page)
					break;
				url = nexturl;
				if (nexturl != null)
					TimeUtil.rest(siteinfo.getDownInterval());

			} catch (Exception e) {
				e.printStackTrace();
				try {
					Systemconfig.dbService.saveLog(siteFlag, key, 3,  url+"\r\n"+e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}
		}
		// //ID大于0表示有相同新闻需要采集
		// if(key.getId() > 0) {
		// for(int i = 0;i< alllist.size();i++) {
		// list.get(i).setId(key.getId());
		// new NewsSameDataCommonDownload(siteFlag,list.get(i), null).run();
		// }
		// } else {
		// dtc.process(alllist, siteinfo.getDownInterval()-5);
		// }
		try {
			Systemconfig.dbService.saveLog(siteFlag, key, 2, totalCount + "", alllist.size() + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		dtc.process(alllist, siteinfo.getDownInterval() - 5, key);
	}

	@Override
	protected void specialHtmlInfo(HtmlInfo html) {
		if (html.getSite().equals("baidu_news_search")) {
			html.setReferUrl("http://news.baidu.com");
			html.setCookie("BAIDUID=AE6A9BCA58B8CEBCF4AC160505049A93:FG=1; favoriteTips=ok; Hm_lvt_e9e114d958ea263de46e080563e254c4=1409106616,1409540228,1409725298,1409800157; BDUSS=Gs1Z3B1NklIU0xSYXZwdGQ5QnlyamlseldVcVYxS0VwRGoxZDFFNlJLRDFycGRUQVFBQUFBJCQAAAAAAAAAAAEAAAD9k1QKd3NncnMwMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPUhcFP1IXBTV; LOCALGX=%u5317%u4EAC%7C%30%7C%u5317%u4EAC%7C%30; BDNVCODE=5407D7C4D634B4056646753; Hm_lpvt_e9e114d958ea263de46e080563e254c4=1409800157; BAIDUID=AE6A9BCA58B8CEBCF4AC160505049A93:FG=1; BD_CK_SAM=1; BDSVRTM=258; H_PS_PSSID=");
		}
	}

}
