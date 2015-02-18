import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import common.bean.CommonData;
import common.bean.EbusinessData;
import common.bean.OwnerData;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

public class testHbase {

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

	public static void main(String[] args) throws Exception {

		// deleteTable("testEbusiness");
		// deleteTable("testEbusiness1");
		// deleteTable("product");
		// deleteTable("owner");
		// //
		//
		// createTableFromEbusinessClass("product");
		// createTableFromEbusinessClass("chuizi");
		// createTableFromOwnerClass("owner");
		// createTableFromOwnerClass("chuizio");

		// showAll("products");

		// getRecord("product", "10033317456");

		// showAll("products");
		// QueryAll("testproductstb");
		// getRecord("products", "0_10631064295");
		// showAll("testp");
		// showColumnValueDistinct("product", "brand", "brand");
		hbaseComment2Oracle("product", "eb_comment");
	}

	/**
	 * hbase 的评论数据导出到oracle
	 */
	public static void hbaseComment2Oracle(String hbaseTable, String oracleTable) {
		HTable h = null;
		try {
			String databaseDriver = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@172.18.21.1:1521:TIRE";
			String user = "tire";
			String pw = "tire2014";
			Class.forName(databaseDriver);
			Connection conn = DriverManager.getConnection(url, user, pw);
			PreparedStatement stmt = null;

			h = new HTable(conf, hbaseTable);
			Scan scan = new Scan(Bytes.toBytes("1_"));
			ResultScanner rs = null;
			rs = h.getScanner(scan);

			int k = 0;
			for (Result result : rs) {
				k++;
				if (k % 100 == 0)
					System.out.println("已处理" + k + " 条评论...");
				if (!Bytes.toString(result.getRow()).startsWith("1_"))
					continue;
				String id = "";
				String info = "";
				String label = "";
				String lv = "";
				String person = "";

				long product_code = 0;

				String product_title = "";
				String pubtime = "";
				Date ts = null;
				String score = "";

				for (KeyValue kv : result.raw()) {
					if (Bytes.toString(kv.getQualifier()).equals("comment_info"))
						info = Bytes.toString(kv.getValue());
					else if (Bytes.toString(kv.getQualifier()).equals("comment_id"))
						id = Bytes.toString(kv.getValue());
					else if (Bytes.toString(kv.getQualifier()).equals("comment_label"))
						label = Bytes.toString(kv.getValue());
					else if (Bytes.toString(kv.getQualifier()).equals("comment_level"))
						lv = Bytes.toString(kv.getValue());
					else if (Bytes.toString(kv.getQualifier()).equals("comment_person"))
						person = Bytes.toString(kv.getValue());
					else if (Bytes.toString(kv.getQualifier()).equals("comment_product"))
						product_title = Bytes.toString(kv.getValue());
					else if (Bytes.toString(kv.getQualifier()).equals("comment_pubtime")) {
						pubtime = Bytes.toString(kv.getValue());
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
						ts = format.parse(pubtime.replace("年", "-").replace("月", "-").replace("日", ""));
					} else if (Bytes.toString(kv.getQualifier()).equals("comment_score"))
						score = Bytes.toString(kv.getValue());
				}

				String rowkey = Bytes.toString(result.getRow());
				if (rowkey.contains("12733791476219028061072"))
					System.out.println(" ");

				if (id.equals(""))
					continue;
				product_code = Long.parseLong(rowkey.replace(id, "").replace("1_", ""));
				String sql = "insert into " + oracleTable + " (" + "INFO, " + "LABEL, " + "LV, " + "PERSON, "
						+ "PRODUCT_CODE, " + "PRODUCT_TITLE, " + "PUBTIME, " + "SCORE, inserttime, cid) " + "values(?,?,?,?,?,?,?,?,?,?)";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, info);
				stmt.setString(2, label);
				stmt.setString(3, lv);
				stmt.setString(4, person);
				stmt.setLong(5, product_code);
				stmt.setString(6, product_title);
				stmt.setTimestamp(7, new Timestamp(ts.getTime()));
				stmt.setString(8, score);
				stmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
				stmt.setLong(10, Long.parseLong(id));
				stmt.execute();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("ok.");
	}

	/**
	 * 打印某列所有不为空的值，不重复
	 * 
	 * @param table
	 * @param family
	 * @param qualifier
	 */
	public static void showColumnValueDistinct(String table, String family, String qualifier) {
		HTable h = null;
		try {
			h = new HTable(conf, table);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scan scan = new Scan();
		// 扫描特定区间
		// Scan scan=new Scan(Bytes.toBytes("开始行号"),Bytes.toBytes("结束行号"));
		ResultScanner scanner = null;
		try {
			scanner = h.getScanner(scan);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HashSet<String> tmp = new HashSet<String>();

		int i = 0;
		for (Result r : scanner) {
			if (Bytes.toString(r.getRow()).startsWith("1_"))
				continue;
			i++;
			System.out.print(i + " ");
			if (i % 100 == 0) {
				System.out.println("\r\n处理：" + i + " 行" + Bytes.toString(r.getRow()) + " list size : " + tmp.size());
			}
			for (KeyValue k : r.raw()) {
				if (!Bytes.toStringBinary(k.getFamily()).startsWith(family))
					continue;
				if (!Bytes.toStringBinary(k.getQualifier()).startsWith(qualifier))
					continue;
				tmp.add(Bytes.toString(k.getValue()));
			}
		}

		for (String string : tmp) {
			System.out.println(string);
		}
		System.out.println("ok.");
	}

	/**
	 * 扫描所有数据或特定数据
	 * 
	 * @param tableName
	 * **/
	public static void showAll(String tableName) throws Exception {

		HTable h = new HTable(conf, tableName);

		Scan scan = new Scan();
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes("md5"), Bytes.toBytes("md5"), CompareOp.NOT_EQUAL,
				Bytes.toBytes(""));
		scan.setFilter(filter);
		// 扫描特定区间
		// Scan scan=new Scan(Bytes.toBytes("开始行号"),Bytes.toBytes("结束行号"));
		ResultScanner scanner = h.getScanner(scan);

		for (Result r : scanner) {
			if (!Bytes.toStringBinary(r.getRow()).startsWith("0_"))
				continue;
			System.out.println("==================================");
			for (KeyValue k : r.raw()) {

				if (!Bytes.toStringBinary(k.getFamily()).startsWith("md5"))
					continue;
				System.out.println("......................................");
				System.out.println("行号:  " + Bytes.toStringBinary(k.getRow()));
				System.out.println("时间戳:  " + k.getTimestamp());
				System.out.println("列簇:  " + Bytes.toStringBinary(k.getFamily()));
				System.out.println("列:  " + Bytes.toStringBinary(k.getQualifier()));
				// if(Bytes.toStringBinary(k.getQualifier()).equals("myage")){
				// System.out.println("值:  "+Bytes.toInt(k.getValue()));
				// }else{
				String ss = Bytes.toString(k.getValue());
				System.out.println("值:  " + ss);
				// }
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			}
		}
		h.close();

	}

	/**
	 * 查询所有数据
	 * 
	 * @param tableName
	 */
	public static void QueryAll(String tableName) {
		try {
			ht = new HTable(conf, tableName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			ResultScanner rs = ht.getScanner(new Scan());

			int i = 0;
			int k = 0;
			for (Result r : rs) {
				i++;
				if (i % 100 == 0)
					System.out.println("总数据量:" + i);

				if (!new String(r.getRow()).startsWith("0_"))
					continue;
				// System.out.println("获得到rowkey:" + new String(r.getRow()));
				HashMap<String, String> kv = new HashMap<String, String>();
				for (KeyValue keyValue : r.raw()) {

					kv.put(new String(keyValue.getFamily()), new String(keyValue.getValue()));
					// System.out.println("列：" + new
					// String(keyValue.getFamily()) + "====值:"
					// + new String(keyValue.getValue()));

				}
				// if(kv.get("title").contains("轮胎")){
				System.err.println("有效数据量：" + k++);
				System.out.println(new String(r.getRow()) + "\t" + kv);
				// }

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ebusiness创建表
	 * 
	 * @param table
	 * @param familys
	 * @throws Exception
	 */
	public static void createTableFromEbusinessClass(String table) throws Exception {
		if (admin.tableExists(table))
			System.out.println("table already exists!");
		else {
			EbusinessData ed = new EbusinessData();
			HTableDescriptor tableDesc = new HTableDescriptor(table);

			for (int i = 0; i < ed.getClass().getDeclaredFields().length; i++) {
				System.out.println(ed.getClass().getDeclaredFields()[i].getName());

				if (!ed.getClass().getDeclaredFields()[i].getName().contains("_")) {
					tableDesc.addFamily(new HColumnDescriptor(ed.getClass().getDeclaredFields()[i].getName()));

				} else {
					String familyName = ed.getClass().getDeclaredFields()[i].getName().split("_")[0];
					String colName = ed.getClass().getDeclaredFields()[i].getName().split("_")[1];
					tableDesc.addFamily(new HColumnDescriptor(familyName));
				}

			}

			CommonData cd = new CommonData();

			for (int i = 0; i < cd.getClass().getDeclaredFields().length; i++) {
				System.out.println(cd.getClass().getDeclaredFields()[i].getName());

				if (!cd.getClass().getDeclaredFields()[i].getName().contains("_")) {
					tableDesc.addFamily(new HColumnDescriptor(cd.getClass().getDeclaredFields()[i].getName()));

				} else {
					String familyName = cd.getClass().getDeclaredFields()[i].getName().split("_")[0];
					String colName = cd.getClass().getDeclaredFields()[i].getName().split("_")[1];

				}
			}

			tableDesc.addFamily(new HColumnDescriptor("update"));
			admin.createTable(tableDesc);

			System.out.println("create table " + table + " OK!");
		}
	}

	/**
	 * owner创建表
	 * 
	 * @param table
	 * @param familys
	 * @throws Exception
	 */
	public static void createTableFromOwnerClass(String table) throws Exception {
		if (admin.tableExists(table))
			System.out.println("table already exists!");
		else {
			OwnerData ed = new OwnerData();
			HTableDescriptor tableDesc = new HTableDescriptor(table);

			for (int i = 0; i < ed.getClass().getDeclaredFields().length; i++) {
				System.out.println(ed.getClass().getDeclaredFields()[i].getName());

				if (!ed.getClass().getDeclaredFields()[i].getName().contains("_")) {
					tableDesc.addFamily(new HColumnDescriptor(ed.getClass().getDeclaredFields()[i].getName()));
				} else {
					String familyName = ed.getClass().getDeclaredFields()[i].getName().split("_")[0];
					String colName = ed.getClass().getDeclaredFields()[i].getName().split("_")[1];
					tableDesc.addFamily(new HColumnDescriptor(familyName));
				}

			}

			CommonData cd = new CommonData();

			for (int i = 0; i < cd.getClass().getDeclaredFields().length; i++) {
				System.out.println(cd.getClass().getDeclaredFields()[i].getName());

				if (!cd.getClass().getDeclaredFields()[i].getName().contains("_")) {
					tableDesc.addFamily(new HColumnDescriptor(cd.getClass().getDeclaredFields()[i].getName()));

				} else {
					String familyName = cd.getClass().getDeclaredFields()[i].getName().split("_")[0];
					String colName = cd.getClass().getDeclaredFields()[i].getName().split("_")[1];
					tableDesc.addFamily(new HColumnDescriptor(familyName));
				}
			}

			admin.createTable(tableDesc);

			System.out.println("create table " + table + " OK!");
		}
	}

	/**
	 * 创建表
	 * 
	 * @param table
	 * @param familys
	 * @throws Exception
	 */
	public static void createTable(String table, String[] familys) throws Exception {
		if (admin.tableExists(table))
			System.out.println("table already exists!");
		else {
			HTableDescriptor tableDesc = new HTableDescriptor(table);
			for (int i = 0; i < familys.length; i++) {

				tableDesc.addFamily(new HColumnDescriptor(familys[i]));

			}
			admin.createTable(tableDesc);
			System.out.println("create table " + table + " OK!");
		}
	}

	/**
	 * 删除表
	 * 
	 * @param table
	 * @throws Exception
	 */
	public static void deleteTable(String table) throws Exception {
		@SuppressWarnings("resource")
		HBaseAdmin admin = new HBaseAdmin(conf);
		admin.disableTable(table);
		admin.deleteTable(table);
		System.out.println("delete table " + table + " OK!");
	}

	/**
	 * 添加记录
	 * 
	 * @param table
	 * @param row
	 * @param family
	 * @param qualifier
	 * @param value
	 * @throws Exception
	 */
	public static void addRecord(String table, String row, String family, String qualifier, String value) throws Exception {
		ht = new HTable(conf, table);
		Put put = new Put(Bytes.toBytes(row));
		put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		ht.put(put);
	}

	/**
	 * 删除记录
	 * 
	 * @param table
	 * @param row
	 * @throws IOException
	 */
	public static void delRecord(String table, String row) throws IOException {
		ht = new HTable(conf, table);
		ArrayList<Delete> list = new ArrayList<Delete>();
		Delete del = new Delete(row.getBytes());
		list.add(del);
		ht.delete(list);
	}

	/**
	 * 获取记录
	 * 
	 * @param table
	 * @param row
	 * @throws IOException
	 */
	public static void getRecord(String table, String row) throws IOException {
		ht = new HTable(conf, table);
		if (row != null) {
			Get get = new Get(row.getBytes());
			Result rs = ht.get(get);
			for (KeyValue kv : rs.raw()) {
				System.out.print(new String(kv.getRow()) + " ");
				System.out.print(new String(kv.getFamily()) + ":");
				System.out.print(new String(kv.getQualifier()) + " ");
				System.out.print(kv.getTimestamp() + " ");
				System.out.println(new String(kv.getValue()));
			}
		} else {
			Scan scan = new Scan();
			ResultScanner rs = ht.getScanner(scan);
			for (Result r : rs) {
				for (KeyValue kv : r.raw()) {
					System.out.print(new String(kv.getRow()) + " ");
					System.out.print(new String(kv.getFamily()) + ":");
					System.out.print(new String(kv.getQualifier()) + " ");
					System.out.print(kv.getTimestamp() + " ");
					System.out.println(new String(kv.getValue()));
				}
			}
		}
	}

}
