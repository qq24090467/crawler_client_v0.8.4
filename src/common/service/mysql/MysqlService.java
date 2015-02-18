package common.service.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import common.rmi.packet.SearchKey;
import common.service.AbstractDBService;
import common.system.Systemconfig;

/**
 * 数据库操作
 * @author grs
 *
 */
public abstract class MysqlService<T> extends AbstractDBService<T> {

	@Override
	public abstract void saveData(T data);
	@Override
	public List<SearchKey> searchKeys() {
		String table = null;
		String col = null;
		switch(Systemconfig.crawlerType) {
		case 1 : 
		case 3 :
		case 5 :
		case 7 :
		case 9 : 
		case 11 :
		case 13 :{
			col = "keyword";
			table = "search_keyword";
		}
		case 2 :
		case 4 :
		case 6 :
		case 8 :
		case 10 : 
		case 12 : 
		case 14 : {
			col = "url";
			table = "monitor_site";
		}
		}
		String sql = null;
		if(Systemconfig.clientinfo != null) {
			sql = "select distinct "+col+", category_code from "+table+" where status=2 limit "+ 
					Systemconfig.clientinfo.getDataStart()+", "+Systemconfig.clientinfo.getDataEnd();
		} else {
			sql = "select distinct "+col+", category_code from "+table+" where status=2";
		}
		
		return this.jdbcTemplate.query(sql, new RowMapper<SearchKey>(){
			@Override
			public SearchKey mapRow(ResultSet rs, int i)
					throws SQLException {
				SearchKey sk = new SearchKey();
				sk.setKey(rs.getString(1));
				sk.setRole(rs.getInt(2));
				return sk;
			}
		});
	}
	
}
