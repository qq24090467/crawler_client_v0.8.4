package common.down;

import java.util.ArrayList;
import java.util.List;

import common.bean.CommonData;
import common.bean.HtmlInfo;
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
public class SimpleMetaCommonDownload extends GenericMetaCommonDownload<CommonData> {

	public SimpleMetaCommonDownload(SearchKey key) {
		super(key);
	}

	@Override
	public void process() {
		List<CommonData> alllist = new ArrayList<CommonData>();
		List<CommonData> list = new ArrayList<CommonData>();
		String url = getRealUrl(siteinfo, gloaburl);
		int page = getRealPage(siteinfo);
		String keyword = key.getKey();
		map.put(keyword, 1);
		String nexturl = url;
		DataThreadControl dtc = new DataThreadControl(siteFlag, keyword);
		HtmlInfo html = htmlInfo("META");
		while (nexturl != null && !nexturl.equals("")) {
			list.clear();

			html.setOrignUrl(nexturl);
			try {
				http.getContent(html);
				nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole() + "");

				if (list.size() == 0) {
					Systemconfig.sysLog.log(url + "元数据页面解析为空！！");
					TimeUtil.rest(siteinfo.getDownInterval());
					break;
				}
				Systemconfig.sysLog.log(url + "元数据页面解析完成。");

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
				break;
			}
		}
		dtc.process(alllist, siteinfo.getDownInterval() - 5, key);
	}

}
