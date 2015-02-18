package common.extractor;

import java.util.List;

import common.bean.HtmlInfo;

public interface Extractor<T> {

	/**
	 * 列表页解析
	 * @param list	解析的列表
	 * @param html	页面信息
	 * @param page	采集页码
	 * @param siteFlag	站点标识
	 * @param collectFlag	采集标识
	 * @param keyword	可扩展的关键词
	 * @return	下一页地址
	 */
	public String templateListPage(List<T> list, HtmlInfo html, int page, String... keyword);
	
	/**
	 * 内容页解析
	 * @param data
	 * @param html
	 * @param page
	 * @param keyword
	 * @return
	 */
	public String templateContentPage(T data, HtmlInfo html, int page, String... keyword);
	
	public String templateContentPage(T data, HtmlInfo html, String... keyword);

}
