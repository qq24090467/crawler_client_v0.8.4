package common.system;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import common.communicate.CopyConfig;
import common.communicate.HeartBeat;
import common.filter.SeedFilter;
import common.rmi.interf.InternalRMI;
import common.rmi.packet.Clientinfo;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.TaskStatus;
import common.service.DBFactory;
import common.service.DBService;
import common.siteinfo.Siteinfo;
import common.up2hdfs.PollLocalFiles;
import common.util.HtmlExtractor;
import common.util.UrlReduplicationRemove;

/**
 * 系统相关配置
 * 
 * @author grs
 * @since 0.5
 */
public class Systemconfig {

	/** 检索达到无销量页面 */
	public static boolean forceStop = false;
	public static int forceStopSales = 0;

	/** 电商品牌代码 */
	public static Map<String, String> ebusinessBrandCode = new HashMap<String, String>();

	/** 注册的站点信息，包括各子站点，字符串标识 */
	public static Map<String, Siteinfo> allSiteinfos = new HashMap<String, Siteinfo>();
	/** 注册的下载类 */
	public static Map<String, String> siteHttpClass = new HashMap<String, String>();
	/** 注册的抽取类 */
	public static Map<String, String> siteExtractClass = new HashMap<String, String>();

	/** 初始化站点线程 */
	public static Map<String, ExecutorService> metaexec = new HashMap<String, ExecutorService>();
	public static Map<String, ExecutorService> dataexec = new HashMap<String, ExecutorService>();
	/** 任务是否完成 */
	public static Map<String, Boolean> finish = new HashMap<String, Boolean>();
	/** 自动抽取 */
	public static HtmlExtractor extractor = new HtmlExtractor();
	public static UrlReduplicationRemove urm;
	/** 系统运行日志 */
	public static LoggerManager sysLog = new LoggerManager(Logger.getLogger("system"));
	/** 文件存储路径 */
	public static String localAddress;
	public static String filePath;
	public static String agentIp;
	public static int agentPort;
	public static boolean createFile;
	public static boolean createPic;
	public static String keywords;
	public static String table;
	/** 读取配置类型，0文件读取，1数据库读取 */
	public static int readConfigType;//

	public static String remote;// HDFS路径
	public static int upThreadNum;// 上传线程数
	public static boolean delLoaclFile;// 删除本地文件
	private int upInterval;// 上传时间间隔(时)
	private boolean needUp;// 是否需要上传

	/** 系统运行前缀 */
	public static String RUN_PREFIX;
	/** 运行的任务 */
	public static final HashMap<String, Future<?>> tasks = new HashMap<String, Future<?>>();
	/** 任务状态 */
	public static Map<String, TaskStatus> taskStatusManager = new HashMap<String, TaskStatus>();

	private static boolean distribute;// 是否使用分布式启动
	/** 客户端信息 */
	public static Clientinfo clientinfo;
	/** 远程接口 */
	public static InternalRMI internalServer;
	public static InternalRMI internalClient;
	/** 心跳线程 */
	private ScheduledExecutorService heatBeat;
	/** 交互线程 */
	private ScheduledExecutorService copyConfig;

	/** 数据库服务 */
	public static DBService dbService;
	public static DBFactory dbFactory;
	/** 爬虫类型 */
	public static int crawlerType;
	/** 爬虫索引(第几个爬虫) */
	private static int clientIndex;
	/** 种子过滤器 */
	public static SeedFilter seedFilter;
	/** 用户管理 */
	public static HashMap<String, List<UserAttr>> users;

	/**
	 * 配置加载完成后，系统初始化操作
	 */
	public void initial() {
		value();
		sysLog.start();
		extractor.init();
		dbService = dbFactory.dbService();
		if (dbService == null) {
			sysLog.log("没有找到相应的数据库服务，系统退出！！");
			System.exit(-1);
		}
		if (distribute) {
			rmiClient();
			clientinfo = new Clientinfo();
			clientinfo.getInfo()[0] = (byte) crawlerType;
			clientinfo.getInfo()[1] = (byte) clientIndex;
			clientinfo.setTime(System.currentTimeMillis());
			try {
				clientinfo.setClientaddress(InetAddress.getLocalHost());
			} catch (UnknownHostException e) {
				e.printStackTrace();
				sysLog.log("未知Host，地址将无法注册", e);
				System.exit(-1);
			}
			try {
				clientinfo = internalClient.regClient(clientinfo);
				sysLog.log(localAddress + "成功注册到server！");
			} catch (Exception e) {
				e.printStackTrace();
				sysLog.log("注册不成功！", e);
				System.exit(-1);
			}
			Systemconfig.localAddress = CrawlerType.getMap().get(crawlerType).getCode() + clientinfo.getInfo()[1];
			heatBeat = Executors.newScheduledThreadPool(1);
			copyConfig = Executors.newScheduledThreadPool(1);
			heatBeat.scheduleAtFixedRate(new HeartBeat(), 5, 5, TimeUnit.SECONDS);
			copyConfig.scheduleAtFixedRate(new CopyConfig(), 5, 5, TimeUnit.SECONDS);
		}
		urm = new UrlReduplicationRemove();

		if (needUp) {
			// 上传本地文件到HDFS
			ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
			exec.scheduleAtFixedRate(new PollLocalFiles(filePath), 24, upInterval, TimeUnit.HOURS);
		}
	}

	/**
	 * 创建数据采集线程池
	 */
	public static void createThreadPool() {
		for (String site : allSiteinfos.keySet()) {
			int num = allSiteinfos.get(site).getThreadNum();
			dataexec.put(site, Executors.newFixedThreadPool(num > 5 ? 5 : num));
		}
	}

	private void value() {
		CrawlerType ct = CrawlerType.getMap().get(crawlerType);
		if (ct != null) {
			RUN_PREFIX = ct.name() + "_";
			table = table == null ? "" : table;
			String str = ct.name().substring(0, ct.name().indexOf("_")).toLowerCase() + "_data";
			table += str.equals("company_data") ? "company_report" : str;
		} else {
			sysLog.log("没有找到相应的采集类型，系统退出！！");
			System.exit(-1);
		}
	}

	private int rmiPort;
	private String rmiName;
	private String serverAddress;

	private void rmiClient() {
		RmiProxyFactoryBean rfb = new RmiProxyFactoryBean();
		rfb.setServiceInterface(InternalRMI.class);
		rfb.setServiceUrl("rmi://" + serverAddress + ":" + rmiPort + "/" + rmiName);
		rfb.setRefreshStubOnConnectFailure(true);
		rfb.setLookupStubOnStartup(false);
		rfb.afterPropertiesSet();
		internalClient = (InternalRMI) rfb.getObject();
	}

	public int start() {
		return clientinfo.getDataStart();
	}

	public int end() {
		return clientinfo.getDataEnd();
	}

	public void setSiteExtractClass(Map<String, String> sitesClassName) {
		Systemconfig.siteExtractClass = sitesClassName;
	}

	public void setSiteHttpClass(Map<String, String> siteDownClass) {
		Systemconfig.siteHttpClass = siteDownClass;
	}

	public void setFilePath(String filePath) {
		Systemconfig.filePath = filePath;
	}

	public void setAgentIp(String agentIp) {
		Systemconfig.agentIp = agentIp;
	}

	public void setAgentPort(int agentPort) {
		Systemconfig.agentPort = agentPort;
	}

	public void setCreateFile(boolean createFile) {
		Systemconfig.createFile = createFile;
	}

	public void setCreatePic(boolean createPic) {
		Systemconfig.createPic = createPic;
	}

	public void setDbFactory(DBFactory factory) {
		Systemconfig.dbFactory = factory;
	}

	public void setKeywords(String keywords) {
		Systemconfig.keywords = keywords;
	}

	public void setTable(String table) {
		Systemconfig.table = table;
	}

	public void setExtractor(HtmlExtractor extractor) {
		Systemconfig.extractor = extractor;
	}

	public void setUpThreadNum(int upThreadNum) {
		Systemconfig.upThreadNum = upThreadNum;
	}

	public void setDelLoaclFile(boolean delLoaclFile) {
		Systemconfig.delLoaclFile = delLoaclFile;
	}

	public void setUpInterval(int upInterval) {
		this.upInterval = upInterval;
	}

	public void setNeedUp(boolean needUp) {
		this.needUp = needUp;
	}

	public void setRemote(String remote) {
		Systemconfig.remote = remote;
	}

	public void setLocalAddress(String localAddress) {
		Systemconfig.localAddress = localAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public void setRmiName(String rmiName) {
		this.rmiName = rmiName;
	}

	public void setRmiPort(int rmiPort) {
		this.rmiPort = rmiPort;
	}

	public void setCrawlerType(int crawlerType) {
		Systemconfig.crawlerType = crawlerType;
	}

	public void setSeedFilter(SeedFilter seedFilter) {
		Systemconfig.seedFilter = seedFilter;
	}

	public void setDistribute(boolean isDistribute) {
		Systemconfig.distribute = isDistribute;
	}

	public static boolean getDistribute() {
		return distribute;
	}

	public void setClientIndex(int clientIndex) {
		Systemconfig.clientIndex = clientIndex;
	}

	public static int getClientIndex() {
		return clientIndex;
	}

	public void setReadConfigType(int readConfigType) {
		Systemconfig.readConfigType = readConfigType;
	}

}
