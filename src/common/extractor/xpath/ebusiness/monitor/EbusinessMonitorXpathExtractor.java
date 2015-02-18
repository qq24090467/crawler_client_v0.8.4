package common.extractor.xpath.ebusiness.monitor;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import common.bean.EbusinessData;
import common.extractor.xpath.XpathExtractor;
import common.siteinfo.Component;

/**
 * 电商垂直抽取实现类
 * @author grs
 */
public class EbusinessMonitorXpathExtractor extends XpathExtractor<EbusinessData> implements EbusinessMonitorExtractorAttribute {

	
	
	@Override
	public void processPage(EbusinessData data, Node domtree,
			Map<String, Component> map, String... args) {
		
	}

	@Override
	public void processList(List<EbusinessData> list, Node domtree,
			Map<String, Component> components, String... args) {
		
	}
	
	@Override
	public void parseUrl(List<EbusinessData> list, Node dom,
			Component component, String... args) {
		
	}

	@Override
	public void parseTitle(List<EbusinessData> list, Node dom,
			Component component, String... args) {
		
	}

}
	