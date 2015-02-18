package common.down.news;

import common.bean.HtmlInfo;
import common.bean.NewsData;
import common.down.DataThreadControl;
import common.down.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载元数据
 * 
 * @author grs
 */
public class NewsMonitorMetaCommonDownload extends GenericMetaCommonDownload<NewsData> {

	public NewsMonitorMetaCommonDownload(SearchKey key) {
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
		String last = null;
		int totalCount = 0;
		while (nexturl != null && !nexturl.equals("")) {
			list.clear();

			html.setOrignUrl(nexturl);
			html.setAgent(false);
			html.setEncode(Systemconfig.allSiteinfos.get(siteFlag).getCharset());

			try {
				http.getContent(html);
				if (last != null)
					if (html.getContent().equals(last))
						break;
				// html.setContent(common.util.StringUtil.getContent("filedown/META/baidu_news_search/6f962c1b7d205db4faf80453362b648e.htm"));
				nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole() + "");
				last = html.getContent();
				if (list.size() == 0) {
					Systemconfig.sysLog.log(url + "元数据页面解析为空！！");
					TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				Systemconfig.sysLog.log(url + "元数据页面解析完成。");
				totalCount += list.size();
				Systemconfig.dbService.getNorepeatData(list, "");
				if (list.size() == 0) {
					Systemconfig.sysLog.log(url + "无新数据。");
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
		try {
			Systemconfig.dbService.saveLog(siteFlag, key, 2, totalCount + "", alllist.size() + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		dtc.process(alllist, siteinfo.getDownInterval(),key);
	}

}
