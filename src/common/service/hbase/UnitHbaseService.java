package common.service.hbase;

import common.service.DBFactory;
import common.service.DBService;
import common.system.Systemconfig;

public class UnitHbaseService implements DBFactory {

	private EbusinessHbaseService eservice;
	
	public void setService(EbusinessHbaseService service) {
		this.eservice = service;
	}
	
	public DBService dbService() {
		switch(Systemconfig.crawlerType) {
		case 1 : 
		case 2 : break;
		case 3 : 
		case 4 : break;
		case 5 :
		case 6 : break;
		case 7 : 
		case 8 : break;
		case 9 :
		case 10 :break;
		case 11 : 
		case 12 : break;
		case 13 : 
		case 14 : return eservice;
		}
		return null;
	}
	
}
