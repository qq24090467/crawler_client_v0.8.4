package common.service.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import common.rmi.packet.SearchKey;
import common.service.AbstractDBService;
import common.system.Systemconfig;

/**
 * 数据库操作
 * 
 * @author grs
 * 
 */
public abstract class OracleService<T> extends AbstractDBService<T> {

	
	
	@Override
	public abstract void saveData(T data);

	@Override
	public List<SearchKey> searchKeys() {
		String table = "search_keyword";
		String col = "keyword";
		String sql = null;
		String clause = " where status=2";
		switch (Systemconfig.crawlerType) {
		case 1: {
			clause += " and type like '%1%'";break;
		}
		case 3:
		case 5:
		case 7:
		case 9:
		case 11:
		case 15: {
			clause += " and category1 != 1 and type like '%1%'";break;
		}
		case 13: {
			clause += " and type like '%2%'";break;
		}
		case 21: {
			clause += " and type like '%3%'";break;
		}
		case 2: 
		case 4:
		case 6:
		case 8:
		case 10:
		case 12:
		case 14:
		case 16:
		case 20: {
			col = "url, site_name";
			table = "monitor_site";
			clause += " and type= "+ ((Systemconfig.crawlerType + 1) % 2) +"and media_type="+((Systemconfig.crawlerType+1) / 2);
			break;
		}
		}
		
		if (Systemconfig.clientinfo != null) {
			sql = "select category_code, " + col + " from (select A." + col
					+ ", A.category_code, rownum rn from (select distinct " + col + ", category_code " + "from "
					+ table + clause + ") A where rownum <= " + Systemconfig.clientinfo.getDataEnd()
					+ ") where rn >" + Systemconfig.clientinfo.getDataStart();
		} else {
			sql = "select category_code, " + col + " from " + table + clause;
		}
		return this.jdbcTemplate.query(sql, new RowMapper<SearchKey>() {
			@Override
			public SearchKey mapRow(ResultSet rs, int i) throws SQLException {
				SearchKey sk = new SearchKey();
				sk.setKey(rs.getString(2));
				sk.setRole(rs.getInt(1));
				if ((Systemconfig.crawlerType + 1) % 2 == 1) {
					sk.setSite(rs.getString(3));
				}
				return sk;
			}
		});
	}

}
