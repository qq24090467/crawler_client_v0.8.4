package common.down.video;

import java.util.ArrayList;
import java.util.List;

import common.bean.VideoData;
import common.bean.HtmlInfo;
import common.down.DataThreadControl;
import common.down.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;

/**
 * 下载元数据
 * @author grs
 */
public class VideoMetaCommonDownload extends GenericMetaCommonDownload<VideoData> {
	
	public VideoMetaCommonDownload(SearchKey key) {
		super(key);
	}
	
	public void process() {
		List<VideoData> alllist = new ArrayList<VideoData>();
		List<VideoData> list = new ArrayList<VideoData>();
		String url = getRealUrl(siteinfo, gloaburl);
		int page = getRealPage(siteinfo);
		String keyword = key.getKey();
		map.put(keyword, 1);
		String nexturl = url;
		DataThreadControl dtc = new DataThreadControl(siteFlag, keyword);
		HtmlInfo html = htmlInfo("META");
		while(nexturl != null && !nexturl.equals("")) {
			list.clear();
			
			html.setOrignUrl(nexturl);
			
			try {
				http.getContent(html);
//					html.setContent(common.util.StringUtil.getContent("filedown/META/baidu/37b30f2108ed06501ad6a769ca8cedc8.htm"));
				
				nexturl = xpath.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole()+"");
				
				if(list.size()==0) {
					Systemconfig.sysLog.log(url + "元数据页面解析为空！！");
					break;
				}
				Systemconfig.sysLog.log(url + "元数据页面解析完成。");
				
				Systemconfig.dbService.getNorepeatData(list, "news_data");
				if(list.size()==0) break;
				alllist.addAll(list);
				
				map.put(keyword, map.get(keyword)+1);
				if(map.get(keyword)>page) break;
				url = nexturl;
				if(nexturl!=null) 
					TimeUtil.rest(siteinfo.getDownInterval());
				
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		dtc.process(alllist, siteinfo.getDownInterval(),key);
	}
}
