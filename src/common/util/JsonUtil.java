package common.util;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 解析json的通用方法
 * @author grs
 * @since 2012.5
 * 
 */
public class JsonUtil {
	/**
	 * 获得解析json文件中的对象列表
	 * 泛型方法
	 *  @param <T>	类
	 * @param data	数据
	 * @param key	关键词
	 * @param cla	类
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> List<T> getJsonList(String data, String key, Class<T> cla) {
		JSONObject jsonObj = JSONObject.fromObject(data);
		return JSONArray.toList(jsonObj.getJSONArray(key), cla);
	}
	/**
	 * 获得json文件中的需要对象
	 * 泛型方法
	 * @param <T>	对象
	 * @param data	数据
	 * @param cla	对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getJsonObject(String data, Class<T> cla) {
		JSONObject jsonObj = JSONObject.fromObject(data);
		return (T) JSONObject.toBean(jsonObj, cla);
	}
	/**
	 *  解析json文件，得到JSONObject，用于后续信息定位
	 * @param data	数据
	 * @param key	需要解析的关键词
	 * @return
	 */
	public static JSONObject getJsonObjectByKey(String data, String key) {
		JSONObject jsonObj = JSONObject.fromObject(data);
		return jsonObj.getJSONObject(key);
	}
	/**
	 *  解析json文件，得到指定字符串
	 * @param data	数据
	 * @param key	需要解析的关键词
	 * @return
	 */
	public static String getStringByKey(String data, String key) {
		JSONObject jsonObj = JSONObject.fromObject(data);
		return jsonObj.getString(key);
	}
}
