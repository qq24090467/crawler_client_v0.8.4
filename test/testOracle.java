import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;

import common.util.MD5Util;

public class testOracle {

	public static void main(String[] args) {
		noname3("eb_comment", "eb_comment_n");
	}

	// 0B7E05FF493408FEC496436F104EAFBD
	// 0b7e05ff493408fec496436f104eafbd

	private static void noname3(String src, String des) {

		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stat = null;
		PreparedStatement exe = null;
		ResultSet rs = null;
		String url = "jdbc:oracle:thin:@172.18.21.1:1521:TIRE";
		try {

			HashMap<Integer, Long> mapOwnerId2Code = new HashMap<Integer, Long>();

			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, "tire", "tire2014");
			stat = conn.createStatement();

			String sql = "select * from " + src;

			rs = stat.executeQuery(sql);
			int count = 0;
			int repeat = 0;
			HashSet<String> md5s = new HashSet<String>();
			System.out.println("inserting...");

			while (rs.next()) {

				int id = rs.getInt("id");
				String md5 = rs.getString("MD5");

				if (md5s.contains(md5)) {
					repeat++;
					continue;
				}
				count++;
				if (count % 100 == 0)
					System.out.println("已入库: " + count + "\t已过滤重复数据: " + repeat);

				String info = rs.getString("INFO");
				String label = rs.getString("LABEL");
				String lv = rs.getString("LV");
				String persons = rs.getString("PERSON");
				Long productCode = rs.getLong("PRODUCT_CODE");

				String productTitle = rs.getString("PRODUCT_TITLE");
				Timestamp pubtime = rs.getTimestamp("PUBTIME");
				String score = rs.getString("SCORE");
				Timestamp insertTime = rs.getTimestamp("INSERTTIME");
				Long cid = rs.getLong("CID");

				String searchKey = rs.getString("SEARCHKEY");
				String productUrl = rs.getString("PRODUCT_URL");

				String inserSql = "insert into "
						+ des
						+ "(INFO, LABEL, LV, PERSON, PRODUCT_CODE, PRODUCT_TITLE, PUBTIME, SCORE, INSERTTIME, CID, SEARCHKEY, PRODUCT_URL, MD5, id) "
						+ "values (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?)";
				exe = conn.prepareStatement(inserSql);
				exe.setString(1, info);
				exe.setString(2, label);
				exe.setString(3, lv);
				exe.setString(4, persons);
				exe.setLong(5, productCode);

				exe.setString(6, productTitle);
				exe.setTimestamp(7, pubtime);
				exe.setString(8, score);
				exe.setTimestamp(9, insertTime);
				exe.setLong(10, cid);

				exe.setString(11, searchKey);
				exe.setString(12, productUrl);
				exe.setString(13, md5);
				exe.setInt(14, id);

				exe.execute();
				md5s.add(md5);

			}
			System.out.println("ok." + count);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stat != null) {
					stat.close();
					stat = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 将eb_data 表中的md5字段更新为md5(url+updatedate)
	 */
	public static void noname2() {

		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stat = null;
		Statement exe = null;
		ResultSet rs = null;
		String url = "jdbc:oracle:thin:@172.18.21.1:1521:TIRE";
		try {

			HashMap<Integer, Long> mapOwnerId2Code = new HashMap<Integer, Long>();

			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, "tire", "tire2014");
			stat = conn.createStatement();

			String sql = "select * from eb_data";

			rs = stat.executeQuery(sql);
			int count = 0;

			System.out.println("updating...");
			while (rs.next()) {
				count++;
				if (count % 100 == 0)
					System.out.println(count);
				int id = rs.getInt("id");

				String url1 = rs.getString("url");
				String yearMonth = rs.getString("year_month");

				String md5 = MD5Util.MD5(url1 + yearMonth);
				exe = conn.createStatement();
				exe.execute("update eb_data set md5=" + md5 + " where id=" + id + "");
			}
			System.out.println("ok.");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stat != null) {
					stat.close();
					stat = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将eb_data 表中的字段更新为产品编码，原为产品表中id
	 */
	public static void noname1() {

		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stat = null;
		Statement exe = null;
		ResultSet rs = null;
		String url = "jdbc:oracle:thin:@172.18.21.1:1521:TIRE";
		try {

			HashMap<Integer, Long> mapOwnerId2Code = new HashMap<Integer, Long>();

			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, "tire", "tire2014");
			stat = conn.createStatement();
			// rs=stat.executeQuery("select * from GLOBAL_DATA");
			rs = stat.executeQuery("select id, code_num from eb_owner");
			int count = 0;
			System.out.println("establishing id codeNum map...");
			while (rs.next()) {
				count++;
				int ownerId = rs.getInt(1);
				String ownerCode = rs.getString(2);
				long ownerCodeInt = Long.parseLong(ownerCode);
				mapOwnerId2Code.put(ownerId, ownerCodeInt);
			}
			System.out.println("ok: " + count);

			String sql = "select * from eb_data";

			rs = stat.executeQuery(sql);
			count = 0;
			System.out.println("updating...");
			while (rs.next()) {
				count++;
				if (count % 100 == 0)
					System.out.println(count);
				int id = rs.getInt("id");
				int ownerId = rs.getInt("owner");
				long ownerIdLong = 0;
				if (mapOwnerId2Code.containsKey(ownerId))
					ownerIdLong = mapOwnerId2Code.get(ownerId);
				exe = conn.createStatement();
				if (ownerIdLong != 0)
					exe.execute("update eb_data set owner=" + ownerIdLong + " where id=" + id + "");
			}
			System.out.println("ok.");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (stat != null) {
					stat.close();
					stat = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}
