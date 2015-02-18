package common;

import common.system.AppContext;
import common.system.Job;
import common.system.Systemconfig;
import common.system.TaskMonitor;

public class CrawlerStart{

	
	public static void main(String[] args) throws Exception {

//		common.util.TimeUtil.rest(8 * 60 * 60);
		
		TaskMonitor tm=new TaskMonitor();
		Thread tmonitor=new Thread(tm);
		tmonitor.start();
		
		AppContext.initAppCtx("");
		if (Systemconfig.getDistribute())
			Job.statusRun();
		else
			Job.simpleRun();
	}

}
