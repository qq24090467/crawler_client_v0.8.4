package common.system;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

/**
 * 日志管理类
 * @author grs
 * @since 2011年12月
 */
public class LoggerManager {
	private final BlockingQueue<String> queue;
	private final BlockingQueue<Throwable> throwQueue;
	private final LogThread thread;
	private volatile boolean isShutdown;
	private volatile int reservations;
	private Logger log;
	
	public LoggerManager(Logger log) {
		queue = new ArrayBlockingQueue<String>(Integer.MAX_VALUE>>16);
		throwQueue = new LinkedBlockingQueue<Throwable>(Integer.MAX_VALUE>>16);
		thread = new LogThread();
		this.log = log;
	}
	public void log(String msg, Throwable throwable) {
		synchronized (this) {
			if(isShutdown) {
				throw new IllegalStateException("服务已关闭！");
			}
			++reservations;
		}
		msg = log.getName() + " : " + msg;
		
		try {
			queue.put(msg);
			if(throwable==null) {
				throwQueue.put(new NULLThrowable());
			} else
				throwQueue.put(throwable);
		} catch (InterruptedException e) {
		}
	}
	public void log(String msg) {
		log(msg, null);
	}
	
	/**
	 * 日志线程启动
	 */
	public void start() {
		thread.start();
	}
	/**
	 * 关闭当前日志线程
	 */
	public void stop() {
		synchronized (this) {
			isShutdown=true;
		}
		thread.interrupt();
	}
	/**
	 * 日志线程内部类，用于管理日志记录
	 * @author Administrator
	 * 
	 */
	private class LogThread extends Thread {
		@Override
		public void run() {
			while(true) {
				try {
					synchronized (LoggerManager.this) {
						if(isShutdown && reservations==0) 
							break;
					}
					String msg = queue.take();
					Throwable throwable = throwQueue.take();
					synchronized (LoggerManager.this) {
						--reservations;
					}
					if(throwable==null||throwable instanceof NULLThrowable)
						log.info(msg);
					else
						log.error(msg, throwable);
				}  catch (InterruptedException e) {
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	public final class NULLThrowable extends Throwable {

		public NULLThrowable() {
		}
	}
	
	public Logger getLog() {
		return log;
	}
	
}