package common.siteinfo;

import java.io.Serializable;
/**
 * 组件数据结构
 * @author grs
 *
 */
@SuppressWarnings("serial")
public class Component implements Serializable {
	private String type;//抽取类型，xpath解析、直接赋值、特殊处理等
	private String name;//属性名
	private String xpath;//Xpath值
	private String prefix;//是否需要前缀，主要用于URL
	private String postfix;//后缀
	private boolean format;//是否需要格式化，只用于字符串
	private int reflect = -1;//是否需要通过反射获得数据，如果需要特殊处理，此属性设置为true
	private boolean unique;//是否作为唯一字段
	private Object value;
	private boolean select;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getPostfix() {
		return postfix;
	}
	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}
	public boolean isFormat() {
		return format;
	}
	public void setFormat(boolean format) {
		this.format = format;
	}
	public int getReflect() {
		return reflect;
	}
	public void setReflect(int reflect) {
		this.reflect = reflect;
	}
	public boolean isUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public boolean getSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}

}