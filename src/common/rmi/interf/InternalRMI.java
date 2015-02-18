package common.rmi.interf;

import java.io.IOException;
import java.util.List;

import common.rmi.packet.ConfigObject;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.TaskStatus;
import common.rmi.packet.Clientinfo;
import common.rmi.packet.ViewInfo;

/**
 * RMI接口,与client交互
 * @author grs
 * @since 2013.6
 */
public interface InternalRMI {
	/**
	 * client心跳检测
	 * @param clientinfo
	 * @throws IOException 
	 */
	public void heartBeat(Clientinfo clientinfo) throws IOException;
	/**
	 * client注册
	 * @param client
	 * @return
	 */
	public Clientinfo regClient(Clientinfo client) throws RegistException;
	/**
	 * web显示的爬虫信息
	 * @param flag
	 * @return
	 */
	List<ViewInfo> crawlerInfo(CrawlerType type, int flag);
	List<ViewInfo> crawlerInfo();
	/**
	 * 获得client状态信息
	 * @param flag	
	 * @return	
	 */
	Clientinfo clientinfo(CrawlerType type, int flag);
	public List<Clientinfo> clientinfo(CrawlerType type);
	public List<Clientinfo> clientinfo();
	/**
	 * client中任务状态
	 * @param taskStatus
	 */
	public void taskStatus(TaskStatus taskStatus);
	/**
	 * 复制配置文件
	 * @param config
	 * @return
	 */
	ConfigObject copyConfig(ConfigObject config);
	
}
