package common.down;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.system.Systemconfig;

/**
 * 下载元数据
 * @author grs
 */
public abstract class GenericMetaCommonDownload<T> extends GenericCommonDownload<T> implements Runnable {
	
	protected Map<String, Integer> map = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	public GenericMetaCommonDownload(SearchKey key) {
		super(key);
	}
	private final CountDownLatch endCount = new CountDownLatch(1);
	@Override
	public void run() {
		prePorcess();
		try {
			process();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			release();
		}
		postProcess();
	}
	public abstract void process();
	
	public void prePorcess() {
		if(Systemconfig.clientinfo != null) {
			ViewInfo vi = Systemconfig.clientinfo.getViewinfos().get(Systemconfig.localAddress+"_"+siteFlag);
			InnerInfo ii = vi.getCrawlers().get(key.getKey());
			ii.setAlive(1);
		}
	}
	public void postProcess() {
		map.clear();
		map = null;
		try {
			endCount.await(5, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		Systemconfig.sysLog.log(siteFlag+"的"+key.getKey()+"数据采集完成！！");
		
		if(Systemconfig.clientinfo != null) {
			ViewInfo vi = Systemconfig.clientinfo.getViewinfos().get(Systemconfig.localAddress+"_"+siteFlag);
			InnerInfo ii = vi.getCrawlers().get(key.getKey());
			ii.setAlive(2);
		}
		Systemconfig.finish.put(siteFlag+key.getKey(), true);
	}
	
	public void release() {
		endCount.countDown();
	}
	
}
