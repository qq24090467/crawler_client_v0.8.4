package common.down.ebusiness;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import common.bean.EbusinessData;
import common.bean.HtmlInfo;
import common.down.GenericDataCommonDownload;
import common.extractor.xpath.ebusiness.search.EbusinessSearchXpathExtractor;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;

public class EbusinessOwnerDownload extends GenericDataCommonDownload<EbusinessData> {

	public EbusinessOwnerDownload(String siteFlag, EbusinessData vd, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}

	@SuppressWarnings("unchecked")
	public void process(EbusinessData ed) {
		// 已经不需要了
		EbusinessSearchXpathExtractor extractor = null;
		try {
			extractor = (EbusinessSearchXpathExtractor) Class.forName(Systemconfig.siteExtractClass.get(siteFlag))
					.newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			HtmlInfo h = new HtmlInfo();
			h.setContent(data.getContent());
			h.setEncode("gbk");
			h.setSite("taobao_ebusiness_search");
			extractor.templateOwnerPage(data, h, 1, new String[] { "" });
			Systemconfig.sysLog.log("卖家解析完成" + ed.getOwner().getOwner_code() + "");
			Systemconfig.dbService.saveData(ed);
			Systemconfig.sysLog.log("卖家保存完成" + ed.getOwner().getOwner_code() + "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Systemconfig.sysLog.log("卖家采集出现异常");
			e.printStackTrace();
		}
	}

	@Override
	public void process() {
		// EbusinessData data, HtmlInfo html, int page, String... keyword
		try {
			HtmlInfo h = new HtmlInfo();
			h.setContent(data.getContent());
			h.setEncode("gbk");
			h.setSite("jd_ebusiness_search");

			Systemconfig.sysLog.log("--------------------------解析卖家开始----------------------");
			((EbusinessSearchXpathExtractor) xpath).templateOwnerPage(data, h, 1, new String[] { "" });
			Systemconfig.sysLog.log("--------------------------卖家解析完成----------------------");
			// try {
			// // Systemconfig.dbService.saveData(data);
			// Systemconfig.sysLog.log("--------------------------卖家保存完成" +
			// data.getOwner().getOwner_code() + "----------------------");
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Systemconfig.dbService.saveLog(siteFlag, key, 3,  data.getUrl()+"\r\n"+e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
