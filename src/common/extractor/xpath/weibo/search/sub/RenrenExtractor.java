package common.extractor.xpath.weibo.search.sub;

import java.util.Date;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.bean.WeiboData;
import common.extractor.xpath.weibo.search.WeiboSearchXpathExtractor;
import common.siteinfo.Component;
import common.util.MD5Util;

public class RenrenExtractor extends WeiboSearchXpathExtractor {

	@Override
	public String parseNext(Node dom, Component component, String... args) {
		String url = args[0];
		int page = Integer.parseInt(args[1]);
		return url = url.substring(0, url.indexOf("offset=")+7) + (page * 99);
	}
	
	@Override
	protected void attrSet(List<WeiboData> list, int siteflag, String key, int code) {
		for(WeiboData vd : list) {
			vd.setSearchKey(key);
			vd.setCategoryCode(code);
			vd.setMd5(MD5Util.MD5(vd.getAuthor()+vd.getPubtime()));
			vd.setSiteId(siteflag);
			vd.setInserttime(new Date());
		}
	}
	
	@Override
	public void parseImgUrl(List<WeiboData> list, Node dom, Component component,
			String... content) {
		String[] arr = null;
		if(component.getXpath().indexOf("|")>-1) {
			arr = component.getXpath().split("\\|");
		}
		String temp = "//DIV[@id]/DIV[contains(@class,'list')][";
		String xpath = "";
		for(int i = 0;i < list.size();i++) {
			if(arr!=null) {
				xpath = temp+(i+1);
				for(int k=0;k<arr.length;k++) {
					xpath += arr[k];
					if(k < arr.length-1) {
						xpath += "|" + temp +(i+1);
					}
				}
			}
			NodeList nl = commonList(xpath, dom);
			if(nl.item(0)!=null)
				list.get(i).setImgUrl(nl.item(0).getTextContent());
		}
	}
}
