package common.service.oracle;

import common.service.DBFactory;
import common.service.DBService;
import common.service.hbase.EbusinessHbaseService;
import common.system.Systemconfig;

public class UnitOracleService implements DBFactory {

	private NewsOracleService newsOracleService;
	private BbsOracleService bbsOracleService;
	private BlogOracleService blogOracleService;
	private WeiboOracleService weiboOracleService;
	private EbusinessHbaseService ebusinessHbaseService;
	private WeixinOracleService weixinOracleService;
	private ReportOracleService reportOracleService;

	public DBService dbService() {
		switch (Systemconfig.crawlerType) {
		case 1:
		case 2:
			return newsOracleService;
		case 3:
		case 4:
			return bbsOracleService;
		case 5:
		case 6:
			return blogOracleService;
		case 7:
		case 8:
			return weiboOracleService;
		case 9:
		case 10:
			return null;
		case 11:
		case 12:
			return null;
		case 13:
		case 14:
			return ebusinessHbaseService;
		case 15:
		case 16:
			return weixinOracleService;
		case 21:
		case 22:
			return reportOracleService;
		}
		return null;
	}

	public void setNewsOracleService(NewsOracleService newsOracleService) {
		this.newsOracleService = newsOracleService;
	}

	public void setWeixinOracleService(WeixinOracleService weixinOracleService) {
		this.weixinOracleService = weixinOracleService;
	}

	public void setBbsOracleService(BbsOracleService bbsOracleService) {
		this.bbsOracleService = bbsOracleService;
	}

	public void setWeiboOracleService(WeiboOracleService weiboOracleService) {
		this.weiboOracleService = weiboOracleService;
	}

	public void setBlogOracleService(BlogOracleService blogOracleService) {
		this.blogOracleService = blogOracleService;
	}

	public void setEbusinessHbaseService(EbusinessHbaseService ebusinessHbaseService) {
		this.ebusinessHbaseService = ebusinessHbaseService;
	}

	public void setReportOracleService(ReportOracleService reportOracleService) {
		this.reportOracleService = reportOracleService;
	}

}
