package common.down.bbs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import oracle.net.aso.e;
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
 * 
 * @author grs
 */
public class BBSMetaCommonDownload extends GenericMetaCommonDownload<BBSData> implements Runnable {
	public BBSMetaCommonDownload(SearchKey key) {
		super(key);
	}

	public void process() {
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
		while (nexturl != null && !nexturl.equals("")) {
			list.clear();

			html.setOrignUrl(nexturl);

			try {
				http.getContent(html);
				// html.setContent(common.util.StringUtil.getContent("filedown/META/baidu/37b30f2108ed06501ad6a769ca8cedc8.htm"));

				nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole() + "");
				totalCount += list.size();
				if (list.size() == 0) {
					Systemconfig.sysLog.log(url + "元数据页面解析为空！！");
					TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				Systemconfig.sysLog.log(url + "元数据页面解析完成。");

				Systemconfig.dbService.getNorepeatData(list, "");
				if (list.size() == 0) {
					TimeUtil.rest(siteinfo.getDownInterval());
					// break;
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

		dtc.process(alllist, siteinfo.getDownInterval(), key);

		// String siteFlag, SearchKey sk, int logType, String... info

	}

	/**
	 * 对不同站点需要的参数进行处理
	 * 
	 * @see common.down.GenericCommonDownload#specialHtmlInfo(common.bean.HtmlInfo)
	 */
	@Override
	protected void specialHtmlInfo(HtmlInfo html) {
		// if (html.getSite().contains("autohome"))
		// html.setCookie("UniqueUserId=786678901341867825; Hm_lvt_531e1bdb569e3bc1bb90698b3cb7d37a=1417744152,1418086520; sessionfid=3772052024; __utma=1.427062411.1417744153.1418086380.1418093023.4; __utmz=1.1418018637.2.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic|utmctr=sagitar; sessionuid=B4C9184C-BF03-D8BA-6C25-978DBDDA4001||2014-12-05+09%3A49%3A11.904; sessionip=159.226.177.188; area=120199; sessionid=B4C9184C-BF03-D8BA-6C25-978DBDDA4001%7C%7C2014-12-05+09%3A49%3A11.904%7C%7C0; ref=alading%7Csagitar%7C1120%7C0%7C2014-12-08+14%3A03%3A58.857%7C2014-12-08+14%3A03%3A58.857; Hm_lvt_90ad5679753bd2b5dec95c4eb965145d=1418018638; AccurateDirectseque=4,3_4_5,103,797,-1; Hm_lpvt_531e1bdb569e3bc1bb90698b3cb7d37a=1418093111; __utmc=1; __utmb=1.0.10.1418093023; sessionvid=C763C6AE-157B-489C-31FA-992DD9F3A37F");
	}
}
