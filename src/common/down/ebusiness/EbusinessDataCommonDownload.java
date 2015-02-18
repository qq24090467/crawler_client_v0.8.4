package common.down.ebusiness;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.w3c.dom.Node;

import com.mysql.jdbc.TimeUtil;

import common.bean.EbusinessData;
import common.bean.HtmlInfo;
import common.down.GenericDataCommonDownload;
import common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor;
import common.extractor.xpath.ebusiness.search.sub.TaobaoExtractor;
import common.http.SimpleHttpProcess;
import common.rmi.packet.SearchKey;
import common.siteinfo.Component;
import common.system.Systemconfig;
import common.util.DOMUtil;
import common.util.StringUtil;

/**
 * 下载详细页面
 * 
 * @author grs
 */
public class EbusinessDataCommonDownload extends GenericDataCommonDownload<EbusinessData> {
	private ExecutorService commentExec = Executors.newFixedThreadPool(1);
	private ExecutorService ownerExec = Executors.newFixedThreadPool(1);

	public EbusinessDataCommonDownload(String siteFlag, EbusinessData vd, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	@SuppressWarnings("unchecked")
	public void process() {
		String site = siteFlag;// jd_ebusiness_search
		String url = getRealUrl(data);
		if (url == null)
			return;
		HtmlInfo html = htmlInfo("DATA");

		// EbusinessCommentDownload ecd=new

		if (url != null && !url.equals("")) {
			try {

				// Systemconfig.sysLog.log(data.getTitle() + "解析开始。。。" + url);
				html.setOrignUrl(url);
				http.getContent(html);

				realContent(html);
				// StringUtil.writeFile("a.htm", html.getContent());
				// html.setContent();
				if (html.getContent() == null) {
					System.out.println("没有下载到页面源代码.");
					return;
				}
				// 解析数据
				// data.setContent(html.getContent());
				xpath.templateContentPage(data, html);
				Systemconfig.sysLog.log(data.getTitle() + "内容页解析完成。。。");

				/* 采集卖家 */

				EbusinessOwnerDownload eod = new EbusinessOwnerDownload(site, data, null, key);
				// 多线程和顺序执行两种方式
				eod.process();
				// Future<?> fOwner = ownerExec.submit(eod);//多线程

				/* 采集评论 */

				// EbusinessCommentDownload ecd = new
				// EbusinessCommentDownload(site, data, null);
				// ecd.process();

				// Future<?> fComment = commentExec.submit(ecd);//多线程

				// try {
				// fOwner.get(120, TimeUnit.SECONDS);//等待，超时结束等待
				// fComment.get(7200, TimeUnit.SECONDS);
				// } catch (InterruptedException e1) {
				// e1.printStackTrace();
				// } catch (ExecutionException e1) {
				// e1.printStackTrace();
				// } catch (TimeoutException e) {
				// e.printStackTrace();
				// }

				// if
				// (data.getBrand().contains(data.getSearchKey().split(" ")[0]))//搜索关键字品牌和抽取到的品牌不一致则不入库

				Systemconfig.dbService.saveData(data);
				Systemconfig.sysLog.log(data.getTitle() + "内容页保存完成。。。");
				synchronized (key) {
					key.savedCountIncrease();
				}
				// if (data.getUrl().contains("tmall.com"))
				// System.out.println("tmall");
			} catch (IOException e) {
				e.printStackTrace();
				try {
					Systemconfig.dbService.saveLog(siteFlag, key, 3, url+"\r\n"+ e.getMessage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}

	}

	/**
	 * 将产品图片简介页源码和原本页面拼接
	 * 
	 * @param html
	 */
	private void realContent(HtmlInfo html) {
		String imgsUrl = "";
		if (html.getOrignUrl().startsWith("http://detail.tmall.com")) {
			if (!html.getContent().contains("\"descUrl\":\""))
				return;
			imgsUrl = StringUtil.regMatcher(html.getContent(), "\"descUrl\":\"", "\"");
		} else if (html.getOrignUrl().startsWith("http://item.taobao.com")) {
			if (!html.getContent().contains("dynamicScript\\(\""))
				return;
			imgsUrl = StringUtil.regMatcher(html.getContent(), "dynamicScript\\(\"", "\"\\)");
		} else {
			return;
		}
		common.util.TimeUtil.rest(2);
		String string = html.getContent();// 原页面源码
		html.setOrignUrl(imgsUrl);//
		http.getContent(html);// 获取图片页源码
		html.setContent("<special>" + html.getContent() + "</special>" + string);// 拼接
	}

	/**
	 * 对不同站点需要的参数进行处理
	 * 
	 * @see common.down.GenericCommonDownload#specialHtmlInfo(common.bean.HtmlInfo)
	 */
	@Override
	protected void specialHtmlInfo(HtmlInfo html) {
		if (html.getSite().equals("taobao_ebusiness_search") || html.getSite().equals("taobao_ebusiness_monitor"))
			html.setCookie("cna=hEjXDG8UJWgCAZ/isbyZYsVA; t=1ca3b1e4428f58d23c1664711aff679e; isg=801042059BA9FF1895120EECAEA1A426; mt=ci%3D-1_0; v=0; cookie2=1a3d98de0d63413628ba903775938653; alitrackid=www.taobao.com; lastalitrackid=www.taobao.com; uc1=cookie14=UoW29wWQiqb3Cw%3D%3D; _tb_token_=737b8ebb15e83; sec=5456cbff085c700f1236d74b52dff30f40be52d1");
		// html.setCookie("cna=WdEyDEM3ZwACAZ/isSfQWDn+; pnm_cku822=072n%2BqZ9mgNqgJnCG0Yus6yybXOuM66wLveft4%3D%7CnOiH84T3i%2FiE%2BIvygfKG8lI%3D%7CneiHGXz1WexE7V%2Fkge6L%2FYnsg%2BZ80GHGdM54Hb0d%7Cmu6b9JHlkuGd7pLuneSX5JDjnu2c6Z3kkeiS6JTtmuKZ4p7lkOmMLA%3D%3D%7Cm%2B%2BT%2FGIWeQ15AXgXYA9q4U3%2BlPFR8Q%3D%3D%7CmO6BH2UZdgVqHmsSaQZxAnUAbxtuFWYJfQ57DGMXYhlqBXkCcdFx%7Cme6d7oHyneiH84Twn%2BmR64TzUw%3D%3D; cq=ccp%3D1; CNZZDATA1000279581=1505491769-1404191320-http%253A%252F%252Fs.taobao.com%252F%7C1409096153; isg=716BF0718A72234035901DF91B18C6F6; otherx=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0; x=__ll%3D-1%26_ato%3D0; t=4a2b7447ecd78b6fa8bb38111e6d4891; tracknick=darkslayer27; cookie2=1d2b02b7631139541099b540074ed4b2; _tb_token_=33167333b677e; uc3=nk2=B0Pu7nPGs6l4QKNM&id2=UUBaDgGeSbBc&vt3=F8dATSPbyfGBJl294wo%3D&lg2=Vq8l%2BKCLz3%2F65A%3D%3D; lgc=darkslayer27; whl=-1%260%260%260; swfstore=115705; __tmall_fp_ab=__804b");
		else if (html.getSite().equals("jd_ebusiness_search") || html.getSite().equals("jd_ebusiness_monitor")) {
			html.setReferUrl("http://item.jd.com/911850.html");
			html.setCookie("__jda=122270672.1097468970.1404088469.1408588362.1408596853.26; __jdv=122270672|p.yiqifa.com|t_1_698282|tuiguang|4db25e0368b24e2eb928b6748234ae5e; __jdu=1097468970; atw=9248.911850.20|1633.1159940517.-3|9882.892921.-3|655.1098277.-3|2643.1000829197.-12|675.775012.-20|842.1028318.-21|12215.627720.-22; ipLoc-djd=1-2800-2850-0; ipLocation=%u5317%u4EAC; aview=9248.911850|9248.1102173|655.1098277|9248.1093024|9882.892921|9882.1008025943|9248.967060|2643.1000829197; bview=3797.10922250|3797.10898799|3379.11232693; btw=3797.10922250.7|3379.11232693.3; __utmz=122270672.1408082162.1.1.utmcsr=trade.jd.com|utmccn=(referral)|utmcmd=referral|utmcct=/order/getOrderInfo.action; un_ex=%7B%22adp%22%3A%22%22%2C%22unt%22%3A%222014-08-08T16%3A35%3A12%22%2C%22stid%22%3A%22698282%22%2C%22wuid%22%3A%224db25e0368b24e2eb928b6748234ae5e%22%2C%22rf%22%3A0%2C%22stp%22%3Anull%2C%22uuid%22%3A%224db25e0368b24e2eb928b6748234ae5e%22%2C%22euid%22%3A%2200f44cafe311ba1ef0f3%22%2C%22unid%22%3A%221%22%7D; unionUnId=1; websiteId=698282; euId=00f44cafe311ba1ef0f3; unt=\"2014-08-08T16:35:12\"; areaId=1; mt_ext=%7b%22adu%22%3a%221d59b49f28b8e1cbfaa92f5f8ecd8cdc%22%7d; user-key=f220ff35-84b4-4b44-be2c-51ef8a778041; cn=0; track=4cd91d51-d962-ad02-6fcb-bc471f5faaf1; _pst=darkslayer27; pin=darkslayer27; unick=darkslayer27; ceshi3.com=A6866F3A7D89BDF03FBC9F4C6EF57FB88EEF8D8335CC5C7A438333153C8A5055DD44876C1E3582DE725DDFCA898893E1CC8F0C5C6499D091E143AF18C8931A032B26CCE26838C8A81CCFB368DA05AF197DD5B9F256027C36578AC925B3EC6A0E2166FBA2E7159BA2C0A3B33771E08A2E3CC41319F54058048679EB020802AEE07DCB45E945B05A9FBA78CB322ACA57BC; _tp=S3TAJ0I%2FpkivnG%2BEec2heA%3D%3D; __utma=122270672.714026442.1408082162.1408082162.1408082162.1; __jdb=122270672.7.1097468970|26.1408596853; __jdc=122270672");
		}
	}

}
