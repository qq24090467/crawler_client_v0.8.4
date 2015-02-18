package common.communicate;

import common.system.Systemconfig;
/**
 * client的心跳检测
 * @author grs
 *
 */
public final class HeartBeat implements Runnable {

	@Override
	public void run() {
		Systemconfig.clientinfo.setTime(System.currentTimeMillis());
		try {
			Systemconfig.internalClient.heartBeat(Systemconfig.clientinfo);
		} catch (Exception e) {
			Systemconfig.sysLog.log("无法链接到远程服务器，请检查服务器是否开启！！", e);
		}
	}
	
}
