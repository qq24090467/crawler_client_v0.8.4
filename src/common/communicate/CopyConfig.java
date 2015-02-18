package common.communicate;

import java.io.File;
import java.util.HashMap;

import common.rmi.packet.ConfigObject;
import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.siteinfo.Siteinfo;
import common.system.AppContext;
import common.system.Job;
import common.system.Systemconfig;
import common.system.AppContext.MyFileFilter;
import common.util.StringUtil;
/**
 * client的配置修改
 * @author grs
 *
 */
public final class CopyConfig implements Runnable {
	
	ConfigObject cf = new ConfigObject("crawler", Systemconfig.localAddress);
	@Override
	public void run() {
		ConfigObject move = Systemconfig.internalClient.copyConfig(cf);
		if(move==null) return;
		
		if(move.getStep()<2) {
			String name = move.getName();
			String type = name.substring(name.indexOf("_")+1);
			String site = name.substring(0, name.indexOf("_"));
			StringBuffer siteStr = new StringBuffer();
			siteStr.append("site").append(File.separator).append(type+"_"+site).append(".xml");
			
			//存在目标IP且需要与当前IP相同再做相应操作
			if(move.getDestIP()!=null && (move.getDestIP().equals(Systemconfig.localAddress))) {
				//删除站点配置
				if(move.getOper()==3) {
					//停止站点所有线程
					HashMap<String, InnerInfo> map = Systemconfig.clientinfo.getViewinfos().get(Systemconfig.localAddress+"_"+move.getName()).getCrawlers();
					for(String sk : map.keySet()) {
						synchronized (Systemconfig.tasks) {
							Systemconfig.tasks.get(name+"_"+sk).cancel(true);
							Systemconfig.tasks.remove(name+"_"+sk);
						}
					}
					synchronized (Systemconfig.clientinfo.getViewinfos()) {
						Systemconfig.clientinfo.getViewinfos().remove(Systemconfig.localAddress+"_"+move.getName());
					}
					synchronized (Systemconfig.allSiteinfos) {
						Systemconfig.allSiteinfos.remove(move.getName());
					}
				} else if(move.getOper()==1) {//新增或修改
					ViewInfo vi = Systemconfig.clientinfo.getViewinfos().get(Systemconfig.localAddress+"_"+move.getName());
					if(vi == null) {
						if(move.getDestSite()==null) return;
						StringUtil.writeFile(siteStr.toString(), move.getDestSite(), "utf-8");
						File f = new File(siteStr.toString());
						AppContext.configSet(f.getName(), move.getDestSite(), f.lastModified());
						File[] fs = new File("config"+File.separator+"site").listFiles(new MyFileFilter());
						if (fs == null || fs.length == 0) {
							Systemconfig.sysLog.log("没有采集的类型配置！");
						} else if (fs.length > 1) {
							Systemconfig.sysLog.log("采集的类型配置超过一个，无法指定！");
						}
						f = fs[0];
						AppContext.configProcess(f.getName(), StringUtil.getContent(f));
					} else {
						HashMap<String, InnerInfo> map = vi.getCrawlers();
						for(String sk : map.keySet()) {
							synchronized (Systemconfig.tasks) {
								Systemconfig.tasks.get(name+"_"+sk).cancel(true);
								Systemconfig.tasks.remove(name+"_"+sk);
							}
						}
					}
					Job.runSite(Systemconfig.allSiteinfos.get(name));
				} else if(move.getOper()==2) {//修改站点属性配置
					Siteinfo si = Systemconfig.allSiteinfos.get(move.getName());
					if(si!=null) {
						si.setThreadNum(move.getThreadNum());
						si.setDownInterval(move.getInterval());
						si.setCycleTime(move.getCycleTime());
						
						ViewInfo vi = Systemconfig.clientinfo.getViewinfos().get(Systemconfig.localAddress+"_"+si.getSiteName());
						vi.setThreadNum(move.getThreadNum());
						vi.setInterval(move.getInterval());
						vi.setCrawlerCycle(move.getCycleTime());
					}
				} else if(move.getOper()==0) {//修改爬虫IP
					java.util.Map<String, ViewInfo> map = Systemconfig.clientinfo.getViewinfos();
					ViewInfo vi = map.get(Systemconfig.localAddress+"_"+move.getName());
					Siteinfo si = null;
					for(SearchKey sk : move.getKeyObj()) {
						InnerInfo ii = new ViewInfo().new InnerInfo();
						ii.setSearchKey(sk);
						ii.setAlive(0);
						if(vi==null) {
							vi = new ViewInfo();
							Job.runInit(si, vi);
							Job.getFirst().put(Systemconfig.localAddress+"_"+move.getName(), vi);
							File f = new File(siteStr.toString());
							AppContext.configSet(f.getName(), StringUtil.getContent(f), f.lastModified());
							File[] fs = new File("config"+File.separator+"site").listFiles(new MyFileFilter());
							if (fs == null || fs.length == 0) {
								Systemconfig.sysLog.log("没有采集的类型配置！");
							} else if (fs.length > 1) {
								Systemconfig.sysLog.log("采集的类型配置超过一个，无法指定！");
							}
							f = fs[0];
							AppContext.configProcess(f.getName(), StringUtil.getContent(f));
						} else {
							if(vi.getCrawlers()==null) 
								vi.setCrawlers(new java.util.HashMap<String, InnerInfo>());
						}
						vi.getCrawlers().put(sk.getKey(), ii);
						//以上只添加成功，还未运行
						Job.runSearchKey(si, sk, vi);
					}
					System.out.println("添加成功");
				}
				move.setStep(2);
				Systemconfig.sysLog.log("执行config step="+move.getStep());
				Systemconfig.internalClient.copyConfig(move);
			}
			//存在来源IP且需要与当前IP相同才复制相应站点数据
			if(move.getSourceIP()!=null && (move.getSourceIP().equals(Systemconfig.localAddress))) {
				if(move.getOper()==0) {
					move.setStep(1);
					Systemconfig.internalClient.copyConfig(move);
				} else if(move.getOper()==1) {
					if(move.getStep()>=1) {
						return;
					} 
					String con = StringUtil.getContent(siteStr.toString());
					move.setDestSite(con);
					move.setType("");
					move.setStep(1);
					Systemconfig.sysLog.log(Systemconfig.localAddress+"完成配置拷贝！");				
					move = Systemconfig.internalClient.copyConfig(move);
				}
			}
		} else if(move.getSourceIP().equals(Systemconfig.localAddress)) {//第二步成功完成，删除来源IP的配置，保证只有一个
			if(move.getOper()==1) {
				move.setStep(3);
				Systemconfig.internalClient.copyConfig(move);
			} else if(move.getOper()==0) {
				HashMap<String, InnerInfo> map = Systemconfig.clientinfo.getViewinfos().get(Systemconfig.localAddress+"_"+move.getName()).getCrawlers();
				if(move.getKeyObj().size()>1) {
					for(SearchKey sk : move.getKeyObj()) {
						synchronized (Systemconfig.tasks) {
							Systemconfig.tasks.get(sk.getSite()+"_"+sk.getKey()).cancel(true);
							Systemconfig.tasks.remove(sk.getSite()+"_"+sk.getKey());
						}
						synchronized (map) {
							map.remove(sk.getKey());
						}
					}
				} else if(move.getKeyObj().size()==1) {
					SearchKey sk = move.getKeyObj().get(0);
					Systemconfig.finish.put(sk.getSite()+sk.getKey(), true);
					if(Systemconfig.tasks.get(sk.getSite()+"_"+sk.getKey())!=null) {
						synchronized (Systemconfig.tasks) {
							Systemconfig.tasks.get(sk.getSite()+"_"+sk.getKey()).cancel(true);
							Systemconfig.tasks.remove(sk.getSite()+"_"+sk.getKey());
						}
					}
					synchronized (map) {
						map.remove(sk.getKey());
					}
				}
				System.out.println("爬虫更换成功！");
				move.setStep(3);
				Systemconfig.internalClient.copyConfig(move);
			}
		}
	}
}
//http://s.taobao.com/search?spm=a230r.1.8.3.5iK3QM&sort=sale-desc&initiative_id=staobaoz_20140825&tab=all&q=http://s.taobao.com/search?data-key=ppath&data-value=20000%3A3227275&data-action=add&ajax=true&_ksTS=1414743213455_1740&callback=jsonp1741&initiative_id=staobaoz_20141031&tab=all&q=%C2%D6%CC%A5&cps=yes&stats_click=search_radio_all%253A1&stats_click=search_radio_all%253A1#J_relative
//http://s.taobao.com/search?spm=a230r.1.8.3.5iK3QM&sort=sale-desc&initiative_id=staobaoz_20140825&tab=all&q=http://s.taobao.com/search?data-key=ppath&data-value=20000%3A20842&data-action=add&ajax=true&_ksTS=1414743631392_2504&callback=jsonp2505&initiative_id=staobaoz_20141031&tab=all&q=%C2%D6%CC%A5&cps=yes&stats_click=search_radio_all%253A1&stats_click=search_radio_all%253A1#J_relative
//http://s.taobao.com/search?spm=a230r.1.8.3.5iK3QM&sort=sale-desc&initiative_id=staobaoz_20140825&tab=all&q=http://s.taobao.com/search?data-key=ppath&data-value=20000%3A20842&data-action=add&ajax=true&_ksTS=1414743631392_2504&callback=jsonp2505&initiative_id=staobaoz_20141031&tab=all&q=%C2%D6%CC%A5&cps=yes&stats_click=search_radio_all%253A1&stats_click=search_radio_all%253A1#J_relative
//http://s.taobao.com/search?data-key=ppath&data-value=20000%3A3227277&data-action=add&ajax=true&_ksTS=1414744898383_4796&callback=jsonp4797&initiative_id=staobaoz_20141031&tab=all&q=%C2%D6%CC%A5&cps=yes&stats_click=search_radio_all%253A1