package common.down.weixin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.bean.HtmlInfo;
import common.bean.WeixinData;
import common.down.DataThreadControl;
import common.down.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.StringUtil;
import common.util.TimeUtil;

/**
 * 下载元数据
 * 
 * @author grs
 */
public class WeixinMetaCommonDownload extends GenericMetaCommonDownload<WeixinData> {

	public WeixinMetaCommonDownload(SearchKey key) {
		super(key);
	}

	@Override
	public void process() {

		List<WeixinData> alllist = new ArrayList<WeixinData>();
		List<WeixinData> list = new ArrayList<WeixinData>();
		String url = getRealUrl(siteinfo, key.getId() > 0 ? key.getKey() : gloaburl);//
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
			try {
				http.getContent(html);
				if (ifStop(html.getContent(), last))
					break;
				// html.setContent(common.util.StringUtil.getContent("filedown/META/baidu_news_search/6f962c1b7d205db4faf80453362b648e.htm"));
				nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole() + "");

				if (list.size() == 0) {
					Systemconfig.sysLog.log(keyword + ": " + url + "元数据页面解析为空！！");
					TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				Systemconfig.sysLog.log(keyword + ": " + url + "元数据页面解析完成。");
				last = html.getContent();
				totalCount += list.size();
				Systemconfig.dbService.getNorepeatData(list, "");
				if (list.size() == 0) {
					Systemconfig.sysLog.log("无新数据。");
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
				break;
			}
		}

		try {
			Systemconfig.dbService.saveLog(siteFlag, key, 2, totalCount + "", alllist.size() + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		dtc.process(alllist, siteinfo.getDownInterval() - 5,key);
	}

	/**
	 * @param curr
	 * @param last
	 * @return false: 不是最后一页; true: 是最后一页
	 */
	protected boolean ifStop(String curr, String last) {
		if (last != null && curr != null) {
			String currPage = StringUtil.regMatcher(curr, "\"page\"", ",");
			String lastPage = StringUtil.regMatcher(last, "\"page\"", ",");
			if (currPage != null && lastPage != null) {
				if (currPage.equals(lastPage)) {
					System.out.println("当前页和上一页内容相同，已是最后一页，退出.");
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected void specialHtmlInfo(HtmlInfo html) {
		if (siteFlag.contains("search")) {
			html.setCookie("IPLOC=CN1200; SUID=D5B1E29F6A20900A0000000054C0975A; SUIR=1421907802; SUV=1421907831559000192; SUID=D5B1E29F7310920A0000000054C0975F; sct=1; ABTEST=0|1422234344|v17; SNUID=3655017BE4E6E96749893275E4735B43; LSTMV=423%2C137; LCLKINT=1206");
			html.setUa("Mozilla/5.0 (Windows NT 5.1; rv:34.0) Gecko/20100101 Firefox/34.0");
		} else if (siteFlag.contains("monitor")) {
			html.setCookie("IPLOC=CN1200; SUID=D5B1E29F6A20900A0000000054C0975A; SUIR=1421907802; SUV=1421907831559000192; SUID=D5B1E29F7310920A0000000054C0975F; sct=1; ABTEST=0|1422237599|v17; SNUID=3655017BE4E6E96749893275E4735B43; LSTMV=0%2C0; LCLKINT=1968");
			html.setReferUrl("http://weixin.sogou.com/weixin?type=1&query=%E4%B8%89%E8%A7%92%E8%BD%AE%E8%83%8E&fr=sgsearch&ie=utf8&_ast=1422236032&_asf=null&w=01029901&cid=null");
			html.setUa("Mozilla/5.0 (Windows NT 5.1; rv:34.0) Gecko/20100101 Firefox/34.0");
		}
	}
}
