package common.service.mysql;

import common.service.DBFactory;
import common.service.DBService;
import common.system.Systemconfig;

public class UnitMysqlService implements DBFactory {
	private NewsMysqlService newsMysqlService;
	private BbsMysqlService bbsMysqlService;
	private BlogMysqlService blogMysqlService;
	private WeiboMysqlService weiboMysqlService;
	@SuppressWarnings("rawtypes")
	public DBService dbService() {
		switch(Systemconfig.crawlerType) {
		case 1 : 
		case 2 : return newsMysqlService;
		case 3 : 
		case 4 : return bbsMysqlService;
		case 5 :
		case 6 : return blogMysqlService;
		case 7 : 
		case 8 : return weiboMysqlService;
		case 9 :
		case 10 : 
		case 11 : 
		case 12 : 
		case 13 : 
		case 14 : 
		}
		return null;
	}
	
	public void setNewsMysqlService(NewsMysqlService newsOracleService) {
		this.newsMysqlService = newsOracleService;
	}
	public void setBbsMysqlService(BbsMysqlService bbsMysqlService) {
		this.bbsMysqlService = bbsMysqlService;
	}
	public void setWeiboMysqlService(WeiboMysqlService weiboMysqlService) {
		this.weiboMysqlService = weiboMysqlService;
	}
	public void setBlogMysqlService(BlogMysqlService blogMysqlService) {
		this.blogMysqlService = blogMysqlService;
	}
	
}
