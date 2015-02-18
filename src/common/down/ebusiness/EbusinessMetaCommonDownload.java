package common.down.ebusiness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.bean.BBSData;
import common.bean.EbusinessData;
import common.bean.HtmlInfo;
import common.down.DataThreadControl;
import common.down.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.TimeUtil;

/**
 * 下载元数据
 * 
 * @author grs
 */
public class EbusinessMetaCommonDownload extends GenericMetaCommonDownload<EbusinessData> implements Runnable {

	public EbusinessMetaCommonDownload(SearchKey key) {
		super(key);
	}

	public void process() {
		List<EbusinessData> alllist = new ArrayList<EbusinessData>();
		List<EbusinessData> list = new ArrayList<EbusinessData>();
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(siteFlag);

		String url = getRealUrl(siteinfo, gloaburl);
		url = siteFlag.contains("jd") ? url.replace("%20%E8%BD%AE%E8%83%8E", "").replace("%E8%BD%AE%E8%83%8E", "")
				.replace("%E5%BE%B7%E5%9B%BD%E9%A9%AC%E7%89%8C", "%E9%A9%AC%E7%89%8C") : url;
		int page = getRealPage(siteinfo);
		String keyword = key.getKey();
		map.put(keyword, 1);
		DataThreadControl dtc = new DataThreadControl(siteFlag, keyword);
		HtmlInfo html = htmlInfo("META");
		
		int currPage = 1;
		Systemconfig.forceStop = false;// 检索达到销量为0的产品所在列表页时，强制false，停止搜索。
		int totalCount = 0;// 检索到的总数
		String lastPageContent = null;
		while (url != null && !url.equals("")) {

			Systemconfig.sysLog.log("正在解析第" + currPage + "页。");
			list.clear();

			html.setOrignUrl(url);

			try {
				http.getContent(html);
				String currPageContent = html.getContent();
				if (currPageContent == null)
					break;
				else {
					if (lastPageContent != null)
						if (currPageContent.equals(lastPageContent)) {
							System.out.println("已达最后页！");
							break;
						}
				}

				// html.setContent(common.util.StringUtil.getContent("filedown/META/baidu/37b30f2108ed06501ad6a769ca8cedc8.htm"));
				String nexturl = xpath.templateListPage(list, html, currPage, keyword, url, key.getRole() + "");
				totalCount += list.size();
				if (list.size() == 0) {
					Systemconfig.sysLog.log(url + "元数据页面解析为空！！" + key.getKey());
					TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				Systemconfig.sysLog.log(url + "元数据页面解析完成。" + key.getKey());

				Systemconfig.dbService.getNorepeatData(list, "");
				if (list.size() == 0) {
					if (alllist.size() == 0)
						TimeUtil.rest(siteinfo.getDownInterval());
					// break;
				}
				alllist.addAll(list);

				map.put(keyword, map.get(keyword) + 1);
				if (map.get(keyword) > page) {
					Systemconfig.sysLog.log("已解析最大列表页数：" + page + ",开始采集。" + key.getKey());
					break;
				}
				if (Systemconfig.forceStop == true) {
					Systemconfig.sysLog.log("列表页检测到销量为" + Systemconfig.forceStopSales + "的产品，停止检索，开始采集" + key.getKey());
					Systemconfig.forceStop = false;
					break;
				}

				currPage++;
				lastPageContent = currPageContent;
				// if (currPage > 1)
				// break;
				if (nexturl != null) {
					url = nexturl;
				}
				TimeUtil.rest(siteinfo.getDownInterval() + (int) (Math.random() * 10));
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Systemconfig.dbService.saveLog(siteFlag, key, 3,  url+"\r\n"+ e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		try {
			Systemconfig.dbService.saveLog(siteFlag, key, 2, totalCount + "", alllist.size() + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		dtc.process(alllist, siteinfo.getDownInterval() - 15,key);
	}
}
