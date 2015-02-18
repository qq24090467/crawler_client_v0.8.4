package common.system;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.xpath.XPathAPI;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import common.rmi.packet.CrawlerType;
import common.siteinfo.Siteinfo;
import common.util.DOMUtil;
import common.util.StringUtil;

/**
 * 系统初始启动
 * 
 * @author grs
 * 
 */
public class AppContext {

	public static ApplicationContext appCtx;

	
	/**
	 * 配置文件的加载及初始化方法
	 * 
	 * @param 配置文件路径
	 */
	public static void initAppCtx(String path) {
		
		initEbusinessBrandCode();//电商垂直商品编码，用于直接设置品牌
		
		PropertyConfigurator.configure(path + "./config/log4j.properties");
		File[] files = new File(path + "config").listFiles();
		ArrayList<String> list = new ArrayList<String>();
		for (File file : files) {
			if (file.getName().startsWith("app")) {
				list.add(path + "config" + File.separator + file.getName());
			}
		}
		String[] arry = new String[list.size()];
		list.toArray(arry);
		appCtx = new FileSystemXmlApplicationContext(arry);

		list.clear();
		files = null;
		arry = null;

		// 读取配置
		switch (Systemconfig.readConfigType) {
		case 0:
			readConfigFromFile();
			break;
		case 1:
			readConfigFromDB();
			break;
		}

		Systemconfig.createThreadPool();
	}

	private static String filepath = "config" + File.separator + "site";
	private static String xpath = "site";
	private static Map<String, FileEntry> map = new HashMap<String, FileEntry>();

	public static Map<String, FileEntry> getMap() {
		return map;
	}

	/**
	 * 加载文件的属性结构
	 * 
	 * @author grs
	 * 
	 */
	class FileEntry {
		String content;
		long modify;
		boolean load = true;
	}

	/**
	 * 文件过滤
	 * 
	 * @author grs
	 * 
	 */
	public static class MyFileFilter implements FileFilter {
		String prefix = CrawlerType.getMap().get(Systemconfig.crawlerType)
				.name().toLowerCase();

		@Override
		public boolean accept(File f) {
			return f.getName().startsWith(prefix)
					&& !f.getName().replace(xpath, "")
							.replace(File.separator, "").startsWith(".") &&
							f.getName().endsWith("xml");
		}
	}

	/**
	 * 从文件读取站点配置
	 */
	public static void readConfigFromFile() {
		File[] xpathFs = new File(xpath).listFiles(new MyFileFilter());
		if (xpathFs == null) {
			Systemconfig.sysLog.log("没有可运行配置站点");
			return;
		}
		for (File f : xpathFs) {
			String content = StringUtil.getContent(f.getAbsolutePath());

			// String name=f.getName();//ebusiness_search_taobao.xml
			// long modified=f.lastModified();//1409798260836
			configSet(f.getName(), content, f.lastModified());
		}
		loadSiteFromFile();
	}

	/**
	 * 加载所有站点公共配置
	 */
	private static void loadSiteFromFile() {
		// 读取简单配置后，处理详细配置
		File[] fs = new File(filepath).listFiles(new MyFileFilter());
		if (fs == null || fs.length == 0) {
			Systemconfig.sysLog.log("没有采集的类型配置！");
			return;
		} else if (fs.length > 1) {
			Systemconfig.sysLog.log("采集的类型配置超过一个，无法指定！");
			return;
		}
		File f = fs[0];// 采集类型： config\site\news_monitor.xml
		// 根据map大小复制数据
		for (String s : map.keySet()) {
			String name = f.getName()
					.substring(0, f.getName().lastIndexOf("."))
					+ "_"
					+ s.substring(s.lastIndexOf("_") + 1, s.length());// ebusiness_search_taobao.xml
			String content = StringUtil.getContent(f.getAbsolutePath());
			configProcess(name, content);
		}
	}

	static  Map<String, SiteTemplateAttr> siteConfigs = null;
	/**
	 * 从数据库中读取模板配置
	 */
	@SuppressWarnings("unchecked")
	public static void readConfigFromDB() {
		// 通过crawlertype过滤出数据库中的站点配置
		siteConfigs = Systemconfig.dbService.getXpathConfig();
		if (siteConfigs == null || siteConfigs.size() == 0) {
			Systemconfig.sysLog.log("没有可运行配置站点");
			return;
		}
		for (SiteTemplateAttr sta : siteConfigs.values()) {
			configSet(sta.getTemplateName()+"_"+sta.getSiteFlag(), sta.getContent(),  sta.getLastModified().getTime());
		}
		loadSiteFromDB();
	}

	public static void loadSiteFromDB() {
		String typeConfig = Systemconfig.dbService.getTypeConfig();
		boolean first = true;
		for (String s : map.keySet()) {
			if(first) {
				File f = new File(filepath+File.separator+CrawlerType.getMap().get(Systemconfig.crawlerType).name().toLowerCase()+".xml");
				if(!f.exists()) 
					StringUtil.writeFile(f.getAbsolutePath(), typeConfig, "utf-8");
				first = false;
			}
			configProcess(s, typeConfig);
		}
	}

	/**
	 * 配置数据结构属性设置，公用
	 * 
	 * @param name
	 *            配置名称
	 * @param content
	 *            配置内容
	 * @param timestamp
	 *            最新修改日期
	 */
	public static void configSet(String name, String content, long timestamp) {
		if (!map.containsKey(name)) {
			FileEntry fe = new AppContext().new FileEntry();
			fe.content = content;
			fe.modify = timestamp;
			map.put(name, fe);
		} else {
			FileEntry fe = map.get(name);
			if (fe.modify != timestamp) {
				fe.content = content;
				fe.modify = timestamp;
				fe.load = true;
			}
		}
	}

	/**
	 * 公有配置处理，公用
	 * 
	 * @param name
	 *            配置名称
	 * @param content
	 *            配置内容
	 */
	public static void configProcess(String name, String content) {
		if (map.containsKey(name)) {
			FileEntry fe = map.get(name);
			if (!fe.load)
				return;

			DOMUtil dom = new DOMUtil();
			Node domtree = dom.ini(fe.content, "utf-8");
			NodeList nameList = null;
			NodeList valueList = null;
			try {
				nameList = XPathAPI.selectNodeList(domtree, "/SITE/PROP/@name");
				valueList = XPathAPI.selectNodeList(domtree,
						"/SITE/PROP/@value");
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < nameList.getLength(); i++) {
				content = content.replace("${"
						+ nameList.item(i).getTextContent() + "}",
						filterCode(valueList.item(i).getTextContent()));
			}
			// 暂时需要特殊处理boolean型属性
			content = content.replace("${agent}", "false").replace("${login}",
					"false");
			String tmp = filepath + File.separator + name + ".temp";
			StringUtil.writeFile(tmp, content);

			loadDynamicBean(tmp);
			fe.load = false;
		}
	}

	private static String filterCode(String str) {
		return str.replace("&", "&amp;").replace("<", "&lt;")
				.replace(">", "&gt;").replace("“", "&quot;");
	}

	private static synchronized void loadDynamicBean(String file) {
		System.out.println("ini:" + file);
		XmlBeanDefinitionReader beanReader = new XmlBeanDefinitionReader(
				((BeanDefinitionRegistry) ((ConfigurableApplicationContext) appCtx)
						.getBeanFactory()));
		beanReader.setResourceLoader(appCtx);
		beanReader.setEntityResolver(new ResourceEntityResolver(appCtx));
		try {
			Resource[] resources = appCtx.getResources(file);
			beanReader.loadBeanDefinitions(resources);
			resources = null;
			String substring = file.substring(file.lastIndexOf(File.separator)+1, file.indexOf("."));// .xml改成了.

			Siteinfo si = (Siteinfo) (appCtx.getBean(substring));
			// 验证站点信息数据是否完整,成功后添加站点
			Systemconfig.allSiteinfos.put(si.getSiteName(), si);
			if(siteConfigs != null && siteConfigs.get(si.getSiteName()) != null) {
				si.setSiteFlag(siteConfigs.get(si.getSiteName()).getId());
			}
			File f = new File(file);
			if (!f.delete()) {
				System.err.println(f + "没有被删除");
			}
			System.out.println("系统初始化站点：" + si);
		} catch (BeansException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void initEbusinessBrandCode(){
		Systemconfig.ebusinessBrandCode.put("20000%3A3227275&", "米其林");
		Systemconfig.ebusinessBrandCode.put("20000%3A20842&", "邓禄普");
		Systemconfig.ebusinessBrandCode.put("20000%3A52914076&", "三角");
		Systemconfig.ebusinessBrandCode.put("20000%3A46110&", "朝阳");
		Systemconfig.ebusinessBrandCode.put("20000%3A53715&", "玲珑");
		Systemconfig.ebusinessBrandCode.put("20000%3A3227276&", "普利司通");
		Systemconfig.ebusinessBrandCode.put("20000%3A3227284&", "德国马牌");
		Systemconfig.ebusinessBrandCode.put("20000%3A3227277&", "固特异");
		
	}

}
