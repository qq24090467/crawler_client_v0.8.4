package common.extractor.xpath.bbs.monitor.sub;

import java.util.List;

import net.sf.json.JSONObject;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.BBSData;
import common.bean.ReplyData;
import common.extractor.xpath.bbs.monitor.BbsMonitorXpathExtractor;
import common.siteinfo.Component;
import common.util.StringUtil;

public class TiebaExtractor extends BbsMonitorXpathExtractor {

	@Override
	public void parsePubtime(BBSData videoData, Node dom,
			Component component, String... args) {
		// component.setXpath("//LI/SPAN[@class='j_reply_data']");
		NodeList nl = commonList(component.getXpath(), dom);
		if (nl == null)
			return;

		if (nl.item(0) != null) {
			String text = nl.item(0).getTextContent();
			String time = "";
			try {
				JSONObject jo = JSONObject.fromObject(text);
				jo = jo.getJSONObject("content");
				time = jo.getString("date");
			} catch (Exception e) {
				String oldXpath=component.getXpath();
				component.setXpath("//LI/SPAN[@class='j_reply_data']");
				nl = commonList(component.getXpath(), dom);
				if (nl == null)
					return;

				if (nl.item(0) != null) {
					time=nl.item(0).getTextContent();
				}
				component.setXpath(oldXpath);
			}
			videoData.setPubtime(time);
			videoData.setPubdate(timeProcess(videoData.getPubtime()));
		}
	}
	
//	@Override
//	public void parseReplytime(List<ReplyData> list, Node dom,
//			Component component, String... strings) {
//		NodeList nl = commonList(component.getXpath(), dom);
//		if(nl==null) return;
////		int l=nl.getLength();
//		judge(list.size(), nl.getLength(), "replytime");
//		for(int i = 0;i < nl.getLength();i++) {
//			String text=nl.item(i).getTextContent();
//			JSONObject jo = JSONObject.fromObject(text);
//			jo = jo.getJSONObject("content");
//			String jotext=jo.toString();
//			list.get(i).setTime(jo.getString("date"));
//			list.get(i).setPubdate(timeProcess(jo.getString("date")));
//		}
//	}
	
	@Override
	public void parseReplyCount(BBSData data, Node domtree,
			Component component, String... ags) {
		NodeList nl = commonList(component.getXpath(), domtree);
		if(nl==null) return;
		if(nl.item(0)!=null) {
			String time = StringUtil.extrator(nl.item(0).getTextContent().split("回复")[0], "\\d");
			if(time==null || time.equals(""))
				data.setReplyCount(0);
			else
				data.setReplyCount(Integer.parseInt(time));
		}
	}
	
}
