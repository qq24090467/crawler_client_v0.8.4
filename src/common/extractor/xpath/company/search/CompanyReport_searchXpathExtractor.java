package common.extractor.xpath.company.search;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import common.bean.ReportData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.Component;
import common.system.Systemconfig;
import common.util.MD5Util;
import common.util.StringUtil;

/**
 * 抽取实现类
 * 
 * @author gxd
 */
public class CompanyReport_searchXpathExtractor extends XpathExtractor<ReportData> implements CompanySearchExtractorAttribute {

	
	@Override
	public void processList(List<ReportData> list, Node domtree, Map<String, Component> comp, String... args) {
		String content = args[0];

		content = content.substring(content.indexOf("[") + 1, content.lastIndexOf("]")).replace("],[", ";")
				.replace("[", "").replace("]", "");

		String[] reportList = content.split(";");

		for (String report : reportList) {
			String[] attrs = report.split(",");
			if (attrs.length < 6)
				continue;
			String stockCode = attrs[0].replace("\"", "");
			String url = "http://www.cninfo.com.cn/" + attrs[1].replace("\"", "");
			String title = attrs[2].replace("\"", "");
			String pubtime = attrs[6].replace("\"", "");

			// System.out.println(stockCode + "\t" + title + "\t" + url + "\t" +
			// pubtime);
			ReportData data = new ReportData();
			data.setTitle(title);
			data.setCompanyId(stockCode);
			data.setPubtime(pubtime);
			data.setTypeId(data.getTypeId(data));			
			
			data.setUrl(url);		
			//path用md5
			data.setPubdate(timeProcess(data.getPubtime().trim()));
		
			list.add(data);
		}

		if (list.size() == 0)
			return;

	}
	
	

	/**
	 * 共有属性设置
	 * 
	 * @param list
	 * @param siteflag
	 * @param key
	 * @param code
	 */
	@Override
	protected void attrSet(List<ReportData> list, int siteflag, String key, int code) {
		for (ReportData data : list) {
			ReportData cd = (ReportData) data;
			cd.setSearchKey(key);
			cd.setCategoryCode(code);
			cd.setMd5(MD5Util.MD5(cd.getUrl()));
			cd.setSiteId(siteflag);
		}
	}

	@Override
	public void processPage(ReportData data, Node domtree, Map<String, Component> map, String... args) {

	}

	@Override
	public void parseUrl(List<ReportData> list, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void parseTitle(List<ReportData> list, Node dom, Component component, String... args) {
		// TODO Auto-generated method stub

	}

}
