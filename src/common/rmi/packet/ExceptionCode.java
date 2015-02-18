package common.rmi.packet;
/**
 * 异常码
 * @author grs
 *
 */
public enum ExceptionCode {
	
	META_NULL,//没有抓取到数据
	META_FAIL,
	META_XPATH_NULL,//没解析出数据
	META_NOT_FOUND,
	META_DOWN_FAST,
	DATA_NULL,//没有抓取到数据
	DATA_FAIL,//失败
	DATA_NOT_FOUND,//没有页面
	DATA_DOWN_FAST;
}
