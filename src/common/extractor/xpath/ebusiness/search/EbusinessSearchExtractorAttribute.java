package common.extractor.xpath.ebusiness.search;

import org.w3c.dom.Node;

import common.bean.EbusinessData;
import common.extractor.ExtractorAttribute;
import common.siteinfo.Component;

public interface EbusinessSearchExtractorAttribute extends ExtractorAttribute<EbusinessData> {

	/**
	 * 价格解析
	 * @param ebd
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parsePrice(EbusinessData ebd, Node dom, Component component, String... args);
	/**
	 * 产品图片解析
	 * @param ebd
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseImgs_product(EbusinessData ebd, Node dom, Component component, String... args);
	/**
	 * 
	 * @param ebd
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseTransation(EbusinessData ebd, Node dom, Component component, String... args);
	/**
	 * 
	 * @param ebd
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseInfo_code(EbusinessData ebd, Node dom, Component component, String... args);
	/**
	 * 
	 * @param ebd
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseInfo_pubtime(EbusinessData ebd, Node dom, Component component, String... args);
	/**
	 * 
	 * @param ebd
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseInfo_type(EbusinessData ebd, Node dom, Component component, String... args);
	/**
	 * 
	 * @param ebd
	 * @param dom
	 * @param component
	 * @param args
	 */
	public void parseParams(EbusinessData ebd, Node dom, Component component, String... args);
	
	/**
	 * 卖家json链接
	 * @param info_code
	 * @return
	 */
	public String getOwnerInitUrl(String info_code);
	
	
	/**
	 * 评论总数
	 * @param content
	 * @return
	 */
	int getCommentCount(String content);
	
	/**
	 * @param ed
	 * @param commentPage
	 * @param args
	 * @return
	 */
	public String templateCommentPage(EbusinessData ed, int commentPage, String... args);
}
