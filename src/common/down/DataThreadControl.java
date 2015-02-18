package common.down;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import common.bean.CommonData;
import common.rmi.packet.SearchKey;
import common.system.Systemconfig;
import common.util.TimeUtil;

/**
 * 详细数据控制线程
 * @author grs
 *
 */
public class DataThreadControl {
	protected final String siteFlag;
	protected final String unique;
	public DataThreadControl(String siteFlag, String unique) {
		this.siteFlag = siteFlag;
		this.unique = unique;
	}
	/**
	 * 处理的第一种方式，列表数据采集一部分就开始采集内容页
	 * @param list
	 * @param interval
	 * @param key 
	 */
	public void process(List list, int interval, SearchKey key) {
		CountDownLatch endCount = new CountDownLatch(list.size());
		Iterator<CommonData> iter = list.iterator();
		int i=0;
		while(iter.hasNext()) {
			// 提交一个url的采集任务
			CommonData vd = iter.next();
			synchronized (list) {
				iter.remove();
			}
			Systemconfig.dataexec.get(siteFlag).execute(DownFactory.dataControl(siteFlag, vd, endCount,key));
			TimeUtil.rest(interval+ (int) (Math.random() * 10));
		}
		try {
			endCount.await(2, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	private static final HashMap<String, List> map = new HashMap<String, List>();
	/**
	 * 处理的第二种方式，站点所有列表数据都采集完成后再采集内容页
	 * @param list
	 * @param interval
	 * @param count
	 */
//	public void process(List list, int interval, long count) {
//		if(count > 1) {
//			synchronized (map) {
//				if(map.containsKey(siteFlag)) {
//					map.get(siteFlag).addAll(list);
//					map.put(siteFlag, map.get(siteFlag));
//				} else
//					map.put(siteFlag, list);
//			}
//			return;
//		}
//		List<CommonData> temp = null;
//		synchronized (map) {
//			if(map.containsKey(siteFlag)) {
//				map.get(siteFlag).addAll(list);
//				map.put(siteFlag, map.get(siteFlag));
//			} else
//				map.put(siteFlag, list);
//			temp = map.get(siteFlag);
//			
//			map.remove(siteFlag);
//		}
//		CountDownLatch endCount = new CountDownLatch(temp.size());
//		Iterator<CommonData> iter = list.iterator();
//		while(iter.hasNext()) {
//			CommonData vd = iter.next();
//			synchronized (list) {
//				iter.remove();
//			}
//			Systemconfig.dataexec.get(siteFlag).execute(DownFactory.dataControl(siteFlag, vd, endCount));
//			TimeUtil.rest(interval);
//		}
//		try {
//			endCount.await(2, TimeUnit.HOURS);
//		} catch (InterruptedException e) {
//			Thread.currentThread().interrupt();
//		}
//	}

}
