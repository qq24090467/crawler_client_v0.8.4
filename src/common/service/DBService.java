package common.service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import common.bean.CommonData;
import common.rmi.packet.SearchKey;
import common.system.SiteTemplateAttr;
import common.system.UserAttr;

/**
 * mysql数据库操作
 * @author Administrator
 *
 */
public interface DBService<T> {

	/**
	 * 将日志保存到数据库，共有4种类型: 1启动 2采集 3异常 4完成 
	 * @param siteFlag
	 * @param sk
	 * @param logType
	 * @param info: type 2, info0 检索数据条数 info1 新数据条数; type 3, info0 异常信息; type 4, info0 入库条数
	 * @throws IOException
	 */
	public void saveLog(String siteFlag, SearchKey sk, int logType, String... info) throws IOException;
	
	/**
	 * 保存数据
	 * @param list
	 * @throws IOException 
	 */
	public void saveDatas(List<T> list) throws IOException;
	public void saveData(T t) throws IOException;
	/**
	 * 删除表中重复的数据
	 * @param url
	 * @param table
	 */
	public void deleteReduplicationUrls(List<String> url, String table);

	/**
	 * 获得表中的md5
	 * @param string
	 * @param map
	 * @return
	 */
	public int getAllMd5(String string, Map<String, List<String>> map);
	/**
	 * 处理异常数据
	 * @param md5
	 * @param table
	 */
	public void exceptionData(String md5, String table);
	/**
	 * 过滤重复数据
	 * @param list
	 * @param table
	 * @return
	 */
	List<? extends CommonData> getNorepeatData(List<? extends CommonData> list,
			String table);
	/**
	 * 数据库中的检索词
	 * @return
	 */
	public List<SearchKey> searchKeys();
	/**
	 * 获得需要登录的网站的用户
	 * @param site 
	 * @return
	 */
	List<UserAttr> getLoginUsers(String site);
	
	/**
	 * 根据crawlerType过滤，获得需采集的站点xpath配置
	 * @return
	 */
	 Map<String, SiteTemplateAttr> getXpathConfig();
	/**
	 * 根据crawlerType过滤，获得采集类型配置
	 * @return
	 */
	String getTypeConfig();
	
	
}