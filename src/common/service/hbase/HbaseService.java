package common.service.hbase;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.jdbc.core.RowMapper;

import common.rmi.packet.SearchKey;
import common.service.AbstractDBService;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;

/**
 * 数据库操作
 * 
 * @author root
 * 
 */
public abstract class HbaseService<T> extends AbstractDBService<T> {

	@Override
	public abstract void saveData(T data) throws IOException;

	@Override
	public List<SearchKey> searchKeys() {
		String table = null;
		String col = null;
		switch (Systemconfig.crawlerType) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 9:
		case 11:
		case 13: {
			col = "keyword";
			table = "search_keyword";
		}
			break;
		case 2:
		case 4:
		case 6:
		case 8:
		case 10:
		case 12:
		case 14: {
			col = "url, site_name";
			table = "monitor_site";
		}
			break;
		}
		// from oracle db
		String sql = null;
		if (Systemconfig.clientinfo != null) {
			sql = "select category_code, " + col + " from (select A." + col
					+ ", A.category_code, rownum rn from (select distinct " + col + ", category_code " + "from "
					+ table + " where status=2 and type like '%2%') A where rownum <= "
					+ Systemconfig.clientinfo.getDataEnd() + ") where rn >" + Systemconfig.clientinfo.getDataStart();
		} else {
			sql = "select category_code, " + col + " from " + table
					+ " where status=2 and type like '%2%'";
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

	private static Configuration conf = null;
	private static HBaseAdmin admin;
	private static HTable ht;
	static {
		Configuration HBASE_CONFIG = new Configuration();
		HBASE_CONFIG.set("hbase.zookeeper.quorum", "192.168.10.94");
		// HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");
		conf = HBaseConfiguration.create(HBASE_CONFIG);
		try {
			admin = new HBaseAdmin(conf);
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		}
	}

	// @Override
	// public int getAllMd5(String table, Map<String, List<String>> map) {
	// int num = 0;
	// boolean fromHbase = false;
	// List<String> list = new ArrayList<String>();
	//
	// if (fromHbase) {
	// try {
	// ht = new HTable(conf, EbusinessHbaseService.getProductTableName());
	// } catch (IOException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	//
	// try {
	// ResultScanner rs = ht.getScanner(new Scan());
	//
	// for (Result r : rs) {
	//
	// if (new String(r.getRow()).startsWith("1_"))
	// break;
	//
	// for (KeyValue k : r.raw()) {
	// if (Bytes.toStringBinary(k.getFamily()).equals("md5")
	// && Bytes.toStringBinary(k.getQualifier()).equals("md5")) {
	// list.add(Bytes.toString(k.getValue()));
	// num++;
	// if (num % 100 == 0)
	// System.out.print(num + " ");
	// if (num % 2000 == 0)
	// System.out.println();
	// }
	// }
	//
	// }
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// try {
	// String SQL_WEB = "select md5 from eb_data";
	// List<String> listOracle = this.jdbcTemplate.queryForList(SQL_WEB,
	// String.class);
	//
	// list.addAll(listOracle);
	// num += listOracle.size();
	// map.put(table, list);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return num;
	// }
}
