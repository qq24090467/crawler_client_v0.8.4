package common.util;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
/**
 * dom初始化类
 * @author grs
 * @since 2011年7月
 */
public class DOMUtil {
	private static Logger LOGGER = Logger.getLogger(DOMUtil.class);
	
	/**
	 * 为进行Xpath解析初始化数据
	 * @param content
	 * @param charset
	 */
	public DocumentFragment ini(String content, String charset) {
		if(content==null) return null;
		charset = charset==null? "utf=8" : charset;
		byte[] byt = null;
		try {
			byt = content.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "").getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("获得字节失败，检查编码是否存在", e);
			byt = null;
			return null;
		}
		InputSource source = new InputSource(new ByteArrayInputStream(byt));
		source.setEncoding(charset);
		DOMFragmentParser parser = new DOMFragmentParser();
		DocumentFragment domtree = new HTMLDocumentImpl().createDocumentFragment();
		try {
			//是否允许增补缺失的标签。如果要以XML方式操作HTML文件，此值必须为真   
			parser.setFeature("http://cyberneko.org/html/features/balance-tags", true);   
			//是否剥掉<script>元素中的<!-- -->等注释符  
			parser.setFeature("http://cyberneko.org/html/features/scanner/script/strip-comment-delims", true);
			parser.parse(source, domtree);
			return domtree;
		} catch (Exception e) {
			LOGGER.error("Dom解析失败，网页数据有误！", e);
			return domtree;
		} finally {
			byt = null;
			source = null;
			parser = null;
		}
	}

	
}