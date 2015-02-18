package common.down;

import java.util.concurrent.CountDownLatch;

import common.rmi.packet.SearchKey;

/**
 * 下载详细页面
 * @author grs
 */
public abstract class GenericDataCommonDownload<T> extends GenericCommonDownload<T> implements Runnable{
	public GenericDataCommonDownload(String siteFlag, T vd, CountDownLatch endCount, SearchKey key) {
		super(siteFlag, vd, endCount, key);
	}
	
	@Override
	public void run() {
		try {
			prePorcess();
			process();
			postProcess();
		} finally {
			release();
		}
	}
	
	public abstract void process();
	
	public void prePorcess() {
	}
	public void postProcess() {
	}
	
	public void release() {
		if(count!=null)
			count.countDown();
	}
	
}
