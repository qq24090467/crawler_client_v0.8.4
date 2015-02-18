import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.w3c.dom.Node;

import common.bean.CommonData;
import common.bean.EbusinessData;
import common.bean.HtmlInfo;
import common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor;
import common.http.SimpleHttpProcess;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.DOMUtil;
import common.util.StringUtil;

public class testXpath {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get("taobao");
		HtmlInfo html = new HtmlInfo();
//		html.setSite("ebusiness_search_taobao");
		
//		String encoding = "gb2312";
		 String encoding="utf-8";
		html.setType("DATA");
		html.setEncode(encoding);
//		html.setCookie("v=0; cookie2=18978d79775c6bd8f9b8f697f8d6026e; uc1=cookie14=UoW29wlaA2AbTQ%3D%3D; _tb_token_=NYcEonrl5jnT; t=15fb9d4a830d5800a89451656f1055e8; cna=v0jXDKH/KBUCAZ/isbxlMFll");
		System.out.println("start..");
		
		
		html.setOrignUrl("http://mp.weixin.qq.com/s?__biz=MzA3MTA0MTEzNQ==&mid=202684450&idx=1&sn=c5b7cde3fbfcd12fbabc3a6f5ec39d1d&3rd=MzA3MDU4NTYzMw==&scene=6#rd");//
		if (html.getContent() == null) {
			SimpleHttpProcess shp = new SimpleHttpProcess();
			shp.getContent(html);
		}
//		System.out.println(html.getContent());
		System.out.println("===============================");
		EbusinessSearchXpathExtractor xpath = new EbusinessSearchXpathExtractor();
		EbusinessData ebd = new EbusinessData();
		Component component = new Component();
		int parseListPageOrContentPage = 1;// 0:list page , 1: content page  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		/*test*/
		component.setXpath("//DIV[@id='img-content']/DIV[@id='js_content']//P");
		

//		List<String> result = StringUtil.regMatches(html.getContent(), "<title>", "/title", true);
//		for (int i = 0; i < result.size(); i++) {
//			System.out.println(i+"\t"+result.get(i));
//			System.out.println(i + "\t" + result.get(i).replace("<title><![CDATA[", "").replace("]]><\\/title", ""));
//		}

		DOMUtil du = new DOMUtil();

		Node dom = du.ini(html.getContent(), encoding);

		List<CommonData> list = new ArrayList<CommonData>();
		if (parseListPageOrContentPage == 0)
			xpath.testListParse(list, dom, component, args);
		else
			xpath.testPageParse(ebd, dom, component, args);
	}
}
