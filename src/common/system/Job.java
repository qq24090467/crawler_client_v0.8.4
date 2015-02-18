package common.system;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import common.down.DownFactory;
import common.down.GenericMetaCommonDownload;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.siteinfo.Siteinfo;
import common.util.StringUtil;
import common.util.TimeUtil;

public class Job {
	/** 线程池管理 */
	private final static Map<String, ExecutorService> execMap = new HashMap<String, ExecutorService>();

	private static Job job = new Job();
	static Map<String, ViewInfo> first = new HashMap<String, ViewInfo>();
	public static List<SearchKey> keys = null;

	/**
	 * true 则跳过不采集
	 * 
	 * @param site
	 * @return
	 */
	private static boolean filter(String site) {
		if (site.contains("sougou"))// site
			return false;
		return true;
	}

	public static Map<String, ViewInfo> getFirst() {
		return first;
	}

	// 包含以下关键字的会采集，否则跳过
	private static String[] filterWord = null;
	// private static String[] filterWord = { "邓禄普" };
	// private static String[] filterWord = { "金宇", "回力", "双钱", "赛轮" };//
	// 淘宝搜索关键词：金宇，回力，双钱，赛轮
	// private static String[] filterWord = { "马牌", "邓禄普", "朝阳", "邓固特异", "米其林",
	// "三角", "普利司通" };// 京东关键词
	// 包含以下关键字的不采集
	private static String[] filterWordN = null;
	// private static String[] filterWordN={"002355","普利司通","米其林","邓禄普"};//
	// private static String[]
	// filterWordN={"3A20842","3A46110","3A3227275","3A52914076"};//
	// 单独关键词，仅测试
	// 值为空时才从数据库读关键词
	// private static String filterKw = "马牌（Continental）_jd";
	private static String filterKw = "";

	// private static String filterKw =
	// "双钱 轮胎_taobao;赛轮 轮胎_taobao;金宇 轮胎_taobao";//
	// 测试单独关键词，必须以下划线分割关键词和站点，如：195R15C轮胎
	// // 8层
	// private static String filterKw =
	// "http://s.taobao.com/search?q=%B4%B8%D7%D3%CA%D6%BB%FA&tab=mall&pspuid=736302&app=detailproduct&fs=1&cat=&loc=%B1%B1%BE%A9&promoted_service2=2&isprepay=1&user_type=1,taobao";
	// // TR645_tb

	/**
	 * 带有运行状态采集
	 * 
	 * @throws Exception
	 */
	public static void statusRun() throws Exception {
		if (Systemconfig.crawlerType % 2 == 1)
			statusSearchRun();
		else
			statusMonitorRun();
	}

	private static void statusSearchRun() throws Exception {
		// 初始化爬虫
		while (true) {
			keys = Systemconfig.dbService.searchKeys();
			Systemconfig.seedFilter.filter(keys);
			System.out.println(keys.size() + "个关键词将采集");
			if (keys != null && keys.size() > 0) {
				for (Siteinfo s : Systemconfig.allSiteinfos.values()) {
					// 每一个站点
					if (filter(s.getSiteName()))
						continue;
					runSite(s);
				}
			}

			if (Systemconfig.crawlerType == CrawlerType.EBUSINESS_SEARCH.ordinal()
					|| Systemconfig.crawlerType == CrawlerType.EBUSINESS_MONITOR.ordinal())
				TimeUtil.rest(30 * 24 * 60 * 60);
			else if (Systemconfig.crawlerType == CrawlerType.NEWS_MONITOR.ordinal()
					|| Systemconfig.crawlerType == CrawlerType.NEWS_MONITOR.ordinal())
				TimeUtil.rest(2 * 60 * 60);
			else
				TimeUtil.rest(6 * 60 * 60);

			if (Systemconfig.readConfigType == 0)
				AppContext.readConfigFromFile();// 每一轮后重新加载一次配置
			else
				AppContext.readConfigFromDB();
		}
	}

	private static void statusMonitorRun() throws Exception {
		// 初始化爬虫
		while (true) {
			keys = Systemconfig.dbService.searchKeys();
			Systemconfig.seedFilter.filter(keys);
			System.out.println(keys.size() + "个关键词将采集");
			if (keys != null && keys.size() > 0) {
				runSite();
			}

			if (Systemconfig.crawlerType == CrawlerType.EBUSINESS_SEARCH.ordinal()
					|| Systemconfig.crawlerType == CrawlerType.EBUSINESS_MONITOR.ordinal())
				TimeUtil.rest(30 * 24 * 60 * 60);
			else if (Systemconfig.crawlerType == CrawlerType.NEWS_MONITOR.ordinal()
					|| Systemconfig.crawlerType == CrawlerType.NEWS_MONITOR.ordinal())
				TimeUtil.rest(2 * 60 * 60);
			else
				TimeUtil.rest(6 * 60 * 60);

			if (Systemconfig.readConfigType == 0)
				AppContext.readConfigFromFile();// 每一轮后重新加载一次配置
			else
				AppContext.readConfigFromDB();
		}
	}

	public static void runSite() {
		Iterator<SearchKey> iter = keys.iterator();
		while (iter.hasNext()) {
			SearchKey sk = iter.next();
			String site = sk.getSite() + "_" + CrawlerType.getMap().get(Systemconfig.crawlerType).name().toLowerCase();
			if (filter(site))
				continue;
			Siteinfo si = Systemconfig.allSiteinfos.get(site);
			sk.setSite(site);
			String key = Systemconfig.localAddress + "_" + site;

			ViewInfo vi = first.get(key);
			if (vi == null) {
				vi = new ViewInfo();
				runInit(si, vi);
				first.put(key, vi);
			}

			sk.setType(Systemconfig.crawlerType);
			if (sk.getIp() == null)
				sk.setIp(Systemconfig.localAddress);
			// 只执行指定为当前IP的数据
			if (!Systemconfig.localAddress.equals(sk.getIp())) {
				synchronized (keys) {
					iter.remove();
				}
				continue;
			}
			// 每个站点属性值设置一次
			vi.setName(site);

			runSearchKey(si, sk, vi);
			// File f = new File("site" + File.separator + site + ".xml");
			// if (f.exists())
			// vi.setFile(StringUtil.getContent(f));
			// vi.setThreadNum(si.getThreadNum());
			// vi.setInterval(si.getDownInterval());
			// vi.setCrawlerCycle(si.getCycleTime());
			//
			// HashMap<String, InnerInfo> inner =
			// Systemconfig.clientinfo.getViewinfos().get(key).getCrawlers();
			// String searchKey = sk.getSite() + sk.getKey();
			// // 该爬虫是初次运行和完成后才会再次执行
			// if (Systemconfig.finish.get(searchKey) == null ||
			// Systemconfig.finish.get(searchKey)) {
			// // 爬虫名和爬虫地址
			// InnerInfo ii = new ViewInfo().new InnerInfo();
			// ii.setSearchKey(sk);
			// ii.setAlive(0);
			// inner.put(sk.getKey(), ii);
			//
			// job.listSearchKey(sk);
			//
			// Systemconfig.finish.put(searchKey, false);
			// TimeUtil.rest(1);
			// }
		}
	}

	/**
	 * 运行某个站点的所有检索词或所属的垂直网址
	 * 
	 * @param si
	 */
	public static void runSite(Siteinfo si) {
		runSite(si, job);
	}

	/**
	 * 指定job运行站点所有内容
	 * 
	 * @param si
	 * @param job
	 */
	public static void runSite(Siteinfo si, Job job) {
		String key = Systemconfig.localAddress + "_" + si.getSiteName();
		ViewInfo vi = first.get(key);
		if (vi == null) {
			vi = new ViewInfo();
			// 初始化
			runInit(si, vi);
			first.put(key, vi);
		}
		// 对每个关键词处理，搜索采集searckey不设置site属性
		Iterator<SearchKey> iter = keys.iterator();
		while (iter.hasNext()) {
			SearchKey sk = iter.next();
			if (sk.getIp() == null)
				sk.setIp(Systemconfig.localAddress);
			// 只执行指定为当前IP的数据
			if (!Systemconfig.localAddress.equals(sk.getIp())) {
				synchronized (keys) {
					iter.remove();
				}
				continue;
			}
			runSearchKey(si, sk, vi);
			TimeUtil.rest(1);
		}
	}

	/**
	 * 爬虫线程运行初始化
	 * 
	 * @param si
	 * @param vi
	 */
	public static void runInit(Siteinfo si, ViewInfo vi) {
		String site = si.getSiteName();

		String key = Systemconfig.localAddress + "_" + site;
		int n = Systemconfig.crawlerType / 2;
		int le = 1;
		if (Systemconfig.crawlerType % 2 != 0) {
			n++;
			le--;
		}
		vi.setBuildType(n);// 类型：
		vi.setStyle(le);// 方式：搜索
		vi.setIp(Systemconfig.localAddress);
		HashMap<String, InnerInfo> crawlers = new HashMap<String, InnerInfo>();// 每个关键词或网址作为一个子爬虫
		vi.setCrawlers(crawlers);

		if (Systemconfig.clientinfo.getViewinfos().get(key) == null) {
			Systemconfig.clientinfo.getViewinfos().put(key, vi);
		}

		if (si.getLogin()) {
			if (Systemconfig.users == null)
				Systemconfig.users = new HashMap<String, List<UserAttr>>();
			if (Systemconfig.users.get(site) == null) {
				List<UserAttr> list = Systemconfig.dbService.getLoginUsers(site);
				Systemconfig.users.put(site, list);
				if (list.size() == 0) {
					Systemconfig.sysLog.log("没有可用账号！本轮采集退出");
					return;
				}
				if (Job.getExecMap().get(site) == null)
					Job.getExecMap().put(site, Executors.newFixedThreadPool(list.size()));
			}
		} else {
			if (Job.getExecMap().get(site) == null)
				Job.getExecMap().put(site, Executors.newFixedThreadPool(si.getThreadNum()));
		}
	}

	/**
	 * 运行站点的某个搜索词或垂直网址
	 * 
	 * @param si
	 * @param sk
	 * @param set
	 * @param vi
	 */
	public static void runSearchKey(Siteinfo si, SearchKey sk, ViewInfo vi) {
		String site = si.getSiteName();
		sk.setSite(site);
		sk.setType(Systemconfig.crawlerType);
		// 每个站点属性值设置一次
		if (vi != null) {
			vi.setName(site);
			String type = site.substring(site.indexOf("_") + 1);// 采集类型
			String name = site.substring(0, site.indexOf("_"));// 站点名
			File f = new File("site" + File.separator + type + "_" + name + ".xml");
			if (f.exists())
				vi.setFile(StringUtil.getContent(f));
			vi.setThreadNum(si.getThreadNum());
			vi.setInterval(si.getDownInterval());
			vi.setCrawlerCycle(si.getCycleTime());
		}

		String searchKey = sk.getSite() + sk.getKey();
		// 该爬虫是初次运行和完成后才会再次执行
		if (Systemconfig.finish.get(searchKey) == null || Systemconfig.finish.get(searchKey)) {
			// 爬虫名和爬虫地址
			InnerInfo ii = new ViewInfo().new InnerInfo();
			ii.setSearchKey(sk);
			ii.setAlive(0);
			vi.getCrawlers().put(sk.getKey(), ii);

			job.listSearchKey(sk);

			Systemconfig.finish.put(searchKey, false);
		}
	}

	/**
	 * 普通搜索采集
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static void simpleSearchRun() {
		Job job = new Job();
		// if (filterKw.contains("_")) {// 测试一个关键词
		// keys = new ArrayList();
		// if (filterKw.contains(";")) {
		// System.out.println("测试n个关键词：" + filterKw);
		// String[] kws = filterKw.split(";");
		// for (String string : kws) {
		// SearchKey sk1 = new SearchKey();
		// sk1.setKey(string.split("_")[0]);
		// sk1.setSite(string.split("_")[1]);
		// keys.add(sk1);
		// }
		// } else {
		// System.out.println("测试一个关键词：" + filterKw);
		// SearchKey sk1 = new SearchKey();
		// sk1.setKey(filterKw.split("_")[0]);
		// sk1.setSite(filterKw.split("_")[1]);
		// keys.add(sk1);
		// }
		// } else {
		// keys = Systemconfig.dbService.searchKeys();
		// }
		while (true) {
			keys = Systemconfig.dbService.searchKeys();
			Systemconfig.sysLog.log(keys.size() + "个关键词将采集:");
			Systemconfig.sysLog.log("当前execmap状态:");
			Systemconfig.sysLog.log(execMap.toString());
			for (SearchKey sk : keys) {
				Systemconfig.sysLog.log(sk.getKey() + ", ");
			}
			ArrayList<String> listRunning = new ArrayList<String>();
			out: for (SearchKey sk : keys) {

				/* 过滤 */
				int flag = 0;
				if (filterWord != null) {
					for (String filterKey : filterWord) {
						if (sk.getKey().contains(filterKey)) {
							flag = 1;
							break;
						}
					}
					if (flag == 1) {
						Systemconfig.sysLog.log("采集：" + sk.getKey() + ".");

					} else {
						Systemconfig.sysLog.log("跳过：" + sk.getKey() + ".");
						continue;
					}
				}
				flag = 0;
				if (filterWordN != null) {
					for (String filterKey : filterWordN) {
						if (sk.getKey().contains(filterKey)) {
							flag = 1;
							break;
						}
					}
					if (flag == 0) {
						Systemconfig.sysLog.log("采集：" + sk.getKey() + ".");

					} else {
						Systemconfig.sysLog.log("跳过：" + sk.getKey() + ".");
						continue;
					}
				}
				/* /过滤 */

				for (String site : Systemconfig.allSiteinfos.keySet()) {
					if (filter(site))
						continue;

					Siteinfo si = Systemconfig.allSiteinfos.get(site);
					sk.setSite(site);
					if (si.getLogin()) {
						// login
						if (Systemconfig.users == null)
							Systemconfig.users = new HashMap<String, List<UserAttr>>();
						if (Systemconfig.users.get(site) == null) {
							List<UserAttr> list = Systemconfig.dbService.getLoginUsers(site);
							Systemconfig.users.put(site, list);
							if (list.size() == 0) {
								Systemconfig.sysLog.log("没有可用账号！本轮采集退出");
								break out;
							}
							if (Job.getExecMap().get(site) == null)
								Job.getExecMap().put(site, Executors.newFixedThreadPool(list.size()));
						}
					} else {
						//
						if (Job.getExecMap().get(site) == null)
							Job.getExecMap().put(site, Executors.newFixedThreadPool(si.getThreadNum()));
					}
					String ss = sk.getSite() + sk.getKey();
					if (Systemconfig.finish.get(ss) == null || Systemconfig.finish.get(ss)) {
						job.listSearchKey(sk);
						listRunning.add(ss);
						Systemconfig.finish.put(ss, false);
						TimeUtil.rest(1);
					}
				}
			}
			if (listRunning.size() == 0) {
				System.out.println("nothing running.");

			} else {
				System.out.println(listRunning);
			}
			if (Systemconfig.crawlerType == CrawlerType.EBUSINESS_SEARCH.ordinal()
					|| Systemconfig.crawlerType == CrawlerType.EBUSINESS_MONITOR.ordinal())
				TimeUtil.rest(30 * 24 * 60 * 60);
			else if (Systemconfig.crawlerType == CrawlerType.NEWS_MONITOR.ordinal()
					|| Systemconfig.crawlerType == CrawlerType.NEWS_MONITOR.ordinal())
				TimeUtil.rest(2 * 60 * 60);
			else
				TimeUtil.rest(6 * 60 * 60);

			if (Systemconfig.readConfigType == 0)
				AppContext.readConfigFromFile();// 每一轮后重新加载一次配置
			else
				AppContext.readConfigFromDB();
		}

	}

	/**
	 * 普通垂直采集
	 */
	@SuppressWarnings("unchecked")
	private static void simpleMonitorRun() {
		Job job = new Job();

		// if (filterKw.contains(",")) {// 测试一个关键词
		// keys=new ArrayList();
		// if (filterKw.contains(";")) {
		// System.out.println("测试n个关键词：" + filterKw);
		// String[] kws = filterKw.split(";");
		// for (String string : kws) {
		// SearchKey sk1 = new SearchKey();
		// sk1.setKey(string.split("_")[0]);
		// sk1.setSite(string.split("_")[1]);
		// keys.add(sk1);
		// }
		// } else {
		// System.out.println("测试一个关键词：" + filterKw);
		// SearchKey sk1 = new SearchKey();
		// sk1.setKey(filterKw.split(",")[0]);
		// sk1.setSite(filterKw.split(",")[1]);
		// keys.add(sk1);
		// }
		// }
		// else {
		// keys = Systemconfig.dbService.searchKeys();
		// }
		while (true) {

			keys = Systemconfig.dbService.searchKeys();
			System.out.println(keys.size() + "个关键词将采集");
			Systemconfig.sysLog.log(keys.size() + "个关键词将采集:");
			Systemconfig.sysLog.log("当前execmap状态:");
			Systemconfig.sysLog.log(execMap.toString());
			for (SearchKey sk : keys) {
				Systemconfig.sysLog.log(sk.getKey() + ", ");
			}
			ArrayList<String> listRunning = new ArrayList<String>();
			out: for (SearchKey sk : keys) {

				/* 过滤 */
				int flag = 0;
				if (filterWord != null) {
					for (String filterKey : filterWord) {
						if (sk.getKey().contains(filterKey)) {
							flag = 1;
							break;
						}
					}
					if (flag == 1) {
						Systemconfig.sysLog.log("采集：" + sk.getKey() + ".");

					} else {
						Systemconfig.sysLog.log("跳过：" + sk.getKey() + ".");
						continue;
					}
				}
				flag = 0;
				if (filterWordN != null) {
					for (String filterKey : filterWordN) {
						if (sk.getKey().contains(filterKey)) {
							flag = 1;// 包含
							break;
						}
					}
					if (flag == 0) {
						Systemconfig.sysLog.log("采集：" + sk.getKey() + ".");

					} else {
						Systemconfig.sysLog.log("跳过：" + sk.getKey() + ".");
						continue;
					}
				}
				/* /过滤 */

				String site = sk.getSite() + "_" + CrawlerType.getMap().get(Systemconfig.crawlerType).name().toLowerCase();
				System.out.println(site + ", " + sk.getKey() + ", " + sk.getSite());
				if (filter(site))
					continue;
				// System.out.println("全部站点信息：");
				// System.out.println(Systemconfig.allSiteinfos);
				Siteinfo si = Systemconfig.allSiteinfos.get(site);

				if (si == null)
					continue;
				System.out.println(sk.getKey());
				sk.setSite(site);
				if (si.getLogin()) {
					if (Systemconfig.users == null)
						Systemconfig.users = new HashMap<String, List<UserAttr>>();
					if (Systemconfig.users.get(site) == null) {
						List<UserAttr> list = Systemconfig.dbService.getLoginUsers(site);
						Systemconfig.users.put(site, list);
						if (list.size() == 0) {
							Systemconfig.sysLog.log("没有可用账号！本轮采集退出");
							break out;
						}
						if (Job.getExecMap().get(site) == null)
							Job.getExecMap().put(site, Executors.newFixedThreadPool(list.size()));
					}
				} else {
					if (Job.getExecMap().get(site) == null)
						Job.getExecMap().put(site, Executors.newFixedThreadPool(si.getThreadNum()));
				}
				String ss = sk.getSite() + sk.getKey();
				if (Systemconfig.finish.get(ss) == null || Systemconfig.finish.get(ss)) {
					job.listSearchKey(sk);
					listRunning.add(ss);
					Systemconfig.finish.put(ss, false);
					TimeUtil.rest(1);
				}
			}
			if (listRunning.size() == 0) {
				System.out.println("nothing running.");

			} else {
				System.out.println(listRunning);
			}
			System.out.println("all keys ok.");
			if (Systemconfig.crawlerType == CrawlerType.EBUSINESS_SEARCH.ordinal()
					|| Systemconfig.crawlerType == CrawlerType.EBUSINESS_MONITOR.ordinal())
				TimeUtil.rest(30 * 24 * 60 * 60);
			else if (Systemconfig.crawlerType == CrawlerType.NEWS_MONITOR.ordinal()
					|| Systemconfig.crawlerType == CrawlerType.NEWS_MONITOR.ordinal())
				TimeUtil.rest(2 * 60 * 60);
			else
				TimeUtil.rest(6 * 60 * 60);

			if (Systemconfig.readConfigType == 0)
				AppContext.readConfigFromFile();// 每一轮后重新加载一次配置
			else
				AppContext.readConfigFromDB();
		}

	}

	public void list(String site, String key) {
		SearchKey skey = new SearchKey();
		skey.setKey(key);
		skey.setSite(site);
		Future<?> f = execMap.get(site).submit(DownFactory.metaControl(skey));
		Systemconfig.tasks.put(site + "_" + key, f);
	}

	public void listSearchKey(SearchKey sk) {
		GenericMetaCommonDownload<?> task=DownFactory.metaControl(sk);
		Future<?> f = execMap.get(sk.getSite()).submit(task);
		Systemconfig.tasks.put(sk.getSite() + "_" + sk.getKey(), f);
		try {
			Systemconfig.dbService.saveLog(sk.getSite(), sk, 1);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			int deadLine = 0;
			if (Systemconfig.crawlerType == CrawlerType.EBUSINESS_SEARCH.ordinal()
					|| Systemconfig.crawlerType == CrawlerType.EBUSINESS_MONITOR.ordinal())
				deadLine = 24;
			else
				deadLine = 12;

			f.get(deadLine, TimeUnit.HOURS);
			task.throwException();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			f.cancel(true);
			try {
				Systemconfig.dbService.saveLog(sk.getSite(), sk, 4, sk.getSavedCount() + "");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Map<String, ExecutorService> getExecMap() {
		return execMap;
	}

	public static Job getJob() {
		return job;
	}

	public static void simpleRun() {
		if (Systemconfig.crawlerType % 2 == 1)
			simpleSearchRun();
		else
			simpleMonitorRun();
	}

}
