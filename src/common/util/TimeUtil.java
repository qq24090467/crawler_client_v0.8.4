package common.util;

import java.util.concurrent.TimeUnit;

/**
 * 时间处理帮助类
 * @author grs
 * @since 2011年7月 
 */
public class TimeUtil {
	
	
	
	
	/**
	 * 休息间隔
	 */
	public static void rest(int num) {
		try {
			TimeUnit.SECONDS.sleep(num);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
}
