import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.w3c.dom.Node;

import common.bean.CommonData;
import common.bean.EbusinessData;
import common.bean.HtmlInfo;
import common.extractor.xpath.SimpleXpathExtractor;
import common.extractor.xpath.XpathExtractor;
import common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor;
import common.http.SimpleHttpProcess;
import common.siteinfo.Component;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.util.DOMUtil;
import common.util.StringUtil;
import common.util.TimeUtil;

public class ProWangPaperDown {
	private static BufferedWriter writer = null;

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		File f = new File("filedown/pro");
		if (!f.exists())
			f.mkdirs();
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("filedown/pro/info.txt", true)));
		while (true) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String TimeString = sdf.format(new java.util.Date());
			run();
			runbaidu();
			runbaidu1();
			runbaidu2();
			runbaidu3();
			TimeUtil.rest(60 * 60);
		}
	}
	
	public static void runbaidu3() {

		HtmlInfo html = new HtmlInfo();
		String encoding = "utf-8";
		html.setType("DATA");
		html.setEncode(encoding);
		html.setOrignUrl("http://www.baidu.com/s?wd=%E6%9C%BA%E5%99%A8%E4%BA%BA%E5%A6%82%E4%BD%95%E5%A4%A7%E8%B7%83%E8%BF%9B&ie=utf-8&cl=3&t=12&fr=news");//
		
		SimpleHttpProcess shp = new SimpleHttpProcess();
		shp.getContent(html);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
		String TimeString = sdf.format(new java.util.Date());
		StringUtil.writeFile("filedown/pro/baidu " + TimeString + " 大跃进 网页.htm", html.getContent());
	}
	
	public static void runbaidu2() {

		HtmlInfo html = new HtmlInfo();
		String encoding = "utf-8";
		html.setType("DATA");
		html.setEncode(encoding);
		html.setOrignUrl("http://www.baidu.com/s?wd=%E6%9C%BA%E5%99%A8%E4%BA%BA%E4%BA%A7%E4%B8%9A%E5%B7%B2%E7%BB%8F%E8%BE%BE%E5%88%B0110%E5%BA%A6%3A%E6%AF%8F%E5%91%A8%E6%96%B0%E7%94%9F%E4%B8%A4%E4%B8%AA%E5%85%AC%E5%8F%B8&ie=utf-8&cl=3&t=12&fr=news");//
		
		SimpleHttpProcess shp = new SimpleHttpProcess();
		shp.getContent(html);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
		String TimeString = sdf.format(new java.util.Date());
		StringUtil.writeFile("filedown/pro/baidu " + TimeString + " 110度 网页.htm", html.getContent());
	}
	
	public static void runbaidu1() {

		HtmlInfo html = new HtmlInfo();
		String encoding = "utf-8";
		html.setType("DATA");
		html.setEncode(encoding);
		html.setOrignUrl("http://news.baidu.com/ns?ct=0&rn=20&ie=utf-8&bs=%E6%9C%BA%E5%99%A8%E4%BA%BA%E5%A6%82%E4%BD%95%E5%A4%A7%E8%B7%83%E8%BF%9B&rsv_bp=1&sr=0&cl=2&f=3&prevct=no&tn=news&word=%E6%9C%BA%E5%99%A8%E4%BA%BA%E4%BA%A7%E4%B8%9A%E5%B7%B2%E7%BB%8F%E8%BE%BE%E5%88%B0110%E5%BA%A6%3A%E6%AF%8F%E5%91%A8%E6%96%B0%E7%94%9F%E4%B8%A4%E4%B8%AA%E5%85%AC%E5%8F%B8&rsv_sug3=2&rsv_sug4=56&rsv_sug1=1&rsv_n=2&rsp=0&inputT=9190&rsv_sug=1");//
		
		SimpleHttpProcess shp = new SimpleHttpProcess();
		shp.getContent(html);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
		String TimeString = sdf.format(new java.util.Date());
		StringUtil.writeFile("filedown/pro/baidu " + TimeString + " 110度.htm", html.getContent());
	}

	public static void runbaidu() {

		HtmlInfo html = new HtmlInfo();
		String encoding = "utf-8";
		html.setType("DATA");
		html.setEncode(encoding);
		html.setOrignUrl("http://news.baidu.com/ns?ct=0&rn=20&ie=utf-8&bs=oracle+limit&rsv_bp=1&sr=0&cl=2&f=8&prevct=no&tn=news&word=%E6%9C%BA%E5%99%A8%E4%BA%BA%E5%A6%82%E4%BD%95%E5%A4%A7%E8%B7%83%E8%BF%9B&rsv_sug3=5&rsv_sug4=88&rsv_sug1=3&rsv_n=2&rsv_sug2=0&inputT=4678");//
		
		SimpleHttpProcess shp = new SimpleHttpProcess();
		shp.getContent(html);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
		String TimeString = sdf.format(new java.util.Date());
		StringUtil.writeFile("filedown/pro/baidu " + TimeString + " 如何大跃进.htm", html.getContent());
	}

	public static void run() {

		// TODO Auto-generated method stub
		HtmlInfo html = new HtmlInfo();
		// html.setSite("ebusiness_search_taobao");

		// String encoding = "gb2312";
		// String encoding="utf-8";
		String encoding = "gbk";
		html.setType("DATA");
		html.setEncode(encoding);
		// html.setCookie("v=0; cookie2=18978d79775c6bd8f9b8f697f8d6026e; uc1=cookie14=UoW29wlaA2AbTQ%3D%3D; _tb_token_=NYcEonrl5jnT; t=15fb9d4a830d5800a89451656f1055e8; cna=v0jXDKH/KBUCAZ/isbxlMFll");
		System.out.println("start..");

		html.setOrignUrl("http://blog.sciencenet.cn/home.php?mod=space&uid=2374&do=blog&id=861070");//
		if (html.getContent() == null) {
			SimpleHttpProcess shp = new SimpleHttpProcess();
			shp.getContent(html);

		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
		String TimeString = sdf.format(new java.util.Date());
		StringUtil.writeFile("filedown/pro/" + TimeString + ".htm", html.getContent(), "gbk");
		// System.out.println(html.getContent());
		System.out.println("===============================");
		SimpleXpathExtractor xpath = new SimpleXpathExtractor();
		CommonData ebd = new CommonData();
		Component component = new Component();
		DOMUtil du = new DOMUtil();
		Node dom = du.ini(html.getContent(), encoding);

		// 开始
		try {
			writer.write("================" + TimeString + "===============\r\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 阅读
		component.setXpath("//SPAN[@class='xg1'][contains(.,'次阅读')]");
		try {
			writer.write(xpath.testPageParse(ebd, dom, component) + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 推荐
		component.setXpath("//H4[@class='bbs pbn']");
		try {
			writer.write(xpath.testPageParse(ebd, dom, component) + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 评论
		html.setOrignUrl("http://blog.sciencenet.cn/comment.php?mod=space&do=blog&uid=2374&id=861070");
		html.setEncode("utf-8");
		SimpleHttpProcess shp = new SimpleHttpProcess();
		shp.getContent(html);
		xpath = new SimpleXpathExtractor();
		ebd = new CommonData();
		component = new Component();
		du = new DOMUtil();
		dom = du.ini(html.getContent(), encoding);
		component.setXpath("//DL[@id]");
		List<CommonData> list1 = new ArrayList<CommonData>();
		try {
			writer.write(xpath.testListParse(list1, dom, component) + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
