package common.system;

import java.util.List;

import common.system.Systemconfig;
import common.util.UserAgent;
/**
 * 用户管理
 * @author grs
 *
 */
public class UserManager {

	/**
	 * 每个用户只能运行一种类型的采集
	 * @param type
	 * @return
	 */
	public synchronized static UserAttr getUser(String siteFlag) {
//		int in = siteFlag.indexOf("_");
//		if(in==-1) in = siteFlag.length();
//		String site = siteFlag.substring(0, in);
		List<UserAttr> list = Systemconfig.users.get(siteFlag);
		if(list == null) return null;
		for(UserAttr ua : list) {
			if(ua.getUsed()>0) continue;
			ua.setUsed(1);
			return ua;
		}
		return null;
	}
	
	public synchronized static void releaseUser(String siteFlag, UserAttr user) {
//		int in = siteFlag.indexOf("_");
//		if(in==-1) in = siteFlag.length();
//		String site = siteFlag.substring(0, in);
		if(user == null) return;
		List<UserAttr> list = Systemconfig.users.get(siteFlag);
		for(UserAttr ua : list) {
			if(ua.equals(user)) {
				ua.setUsed(0);
				UserAgent.releaseUserAgent(ua.getId());
			}
		}
	}
	
}
