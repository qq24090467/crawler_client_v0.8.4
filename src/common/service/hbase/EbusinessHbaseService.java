package common.service.hbase;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.sun.istack.internal.FinalArrayList;

import sun.misc.OSEnvironment;
import common.bean.CommentData;
import common.bean.CommonData;
import common.bean.EbusinessData;
import common.bean.NewsData;
import common.bean.OwnerData;
import common.system.Systemconfig;
import common.util.StringUtil;

public class EbusinessHbaseService extends HbaseService<EbusinessData> {

	private static Configuration conf = null;
	static HConnection connection = null;
	private static HTable ht;
	private static HBaseAdmin admin;
	private static String[] productsFields;
	private static String[] ownersFields;
	private static String[] superFields;
	private static String[] commentFields;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	static {
		Configuration HBASE_CONFIG = new Configuration();
		HBASE_CONFIG.set("hbase.zookeeper.quorum", "192.168.10.94");
		// HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");

		conf = HBaseConfiguration.create(HBASE_CONFIG);
		try {
			connection = HConnectionManager.createConnection(conf);
			admin = new HBaseAdmin(connection);
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		}

		Field[] ownerField = OwnerData.class.getDeclaredFields();
		Field[] productField = EbusinessData.class.getDeclaredFields();
		Field[] superField = EbusinessData.class.getSuperclass().getDeclaredFields();
		Field[] commentField = CommentData.class.getDeclaredFields();

		productsFields = new String[productField.length];
		ownersFields = new String[ownerField.length];
		superFields = new String[superField.length];
		commentFields = new String[commentField.length];
		for (int i = 0; i < productField.length; i++) {

			String s = productField[i].getName().substring(productField[i].getName().indexOf(".") + 1);
			productsFields[i] = s;

		}
		for (int i = 0; i < ownerField.length; i++) {
			String s = ownerField[i].getName().substring(ownerField[i].getName().indexOf(".") + 1);
			ownersFields[i] = s;
		}
		for (int i = 0; i < superField.length; i++) {
			String s = superField[i].getName().substring(superField[i].getName().indexOf(".") + 1);
			// productsFields[i] = s;
			// ownersFields[i] = s;
			superFields[i] = s;
		}
		for (int i = 0; i < commentField.length; i++) {
			String s = commentField[i].getName().substring(commentField[i].getName().indexOf(".") + 1);
			commentFields[i] = s;
		}

	}

	private static final String product_table = "product";
	private static final String owner_table = "owner";

	private static final String oracle_product_table = "eb_data";
	private static final String oracle_comment_table = "eb_comment";
	private static final String oracle_owner_table = "eb_owner";

	// private static final String product_table = "testp";
	// private static final String owner_table = "testo";

	public static String getProductTableName() {
		return product_table;
	}

	public static String getOwnerTableName() {
		return owner_table;
	}

	// private static final String product_table = "testproductstb";
	// private static final String owner_table = "testownerstb";

	// private static final String product_table = "testproductsjd";
	// private static final String owner_table = "testownersjd";

	public static String getOracleProductTable() {
		return oracle_product_table;
	}

	public static String getOracleCommentTable() {
		return oracle_comment_table;
	}

	public static String getOracleOwnerTable() {
		return oracle_owner_table;
	}

	private void createTablesIfNotExist() {
		try {
			createTableFromEbusinessClass(product_table);
			createTableFromOwnerClass(owner_table);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static final String jasql = "insert into " + oracle_product_table + "(" + "title, " + "brand," + "content,"
			+ "product_img," + "info_img," +

			"insert_time," + "diameter," + "width," + "price," + "sale_num," +

			"name," + "url," + "info, " + "category_code," + "md5,"

			+ "search_keyword," + "site_id," + "year_month," + "owner," + "model,"

			+ "code_num," + "company" + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String ownersql = "insert into " + oracle_owner_table + " " + "("
			+ "company, address, name, url, pscore, sscore, ascore, code_num, inserttime, searchkey, product_url" + ") "
			+ "values " + "(?,?,?,?,?,?,?,?,?,?,?)";

	public void saveDataOra(final EbusinessData vd) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(jasql, new String[] { "id" });
				ps.setString(1, vd.getTitle());
				ps.setString(2, vd.getBrand());
				ps.setString(3, vd.getContent() == null ? "" : vd.getContent());
				ps.setString(4, vd.getImgs_product());
				ps.setString(5, vd.getImgs_info());

				ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
				ps.setString(7, vd.getParams_diameter());
				ps.setString(8, vd.getParams_width());
				ps.setString(9, vd.getPrice());
				ps.setString(10, vd.getTransation());

				ps.setString(11, vd.getName());
				ps.setString(12, vd.getUrl());
				ps.setString(13, vd.getParams_params());
				ps.setInt(14, vd.getCategoryCode());
				ps.setString(15, vd.getMd5());

				ps.setString(16, vd.getSearchKey());
				ps.setInt(17, vd.getSiteId());
				ps.setString(18, vd.getUpdateDate());
				ps.setLong(19, Long.parseLong(vd.getOwner().getOwner_code()));
				ps.setString(20, vd.getParams_model());

				ps.setString(21, vd.getInfo_code());
				ps.setString(22, vd.getCompany());
				return ps;
			}
		}, keyHolder);
		vd.setId(Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d")));

	}

	/**
	 * @param od
	 * @param args
	 *            : 0: searchkey, 1: product url
	 */
	public void saveOwnerOra(final OwnerData od, final String... args) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(ownersql, new String[] { "id" });
				ps.setString(1, od.getOwner_company());
				ps.setString(2, od.getOwner_address());
				ps.setString(3, od.getOwner_name());
				ps.setString(4, od.getOwner_url());
				ps.setString(5, od.getOwner_pScore());
				ps.setString(6, od.getOwner_sScore());
				ps.setString(7, od.getOwner_score());
				ps.setLong(8, Long.parseLong(od.getOwner_code()));
				ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
				ps.setString(10, args[0]);
				ps.setString(11, args[1]);
				return ps;
			}
		}, keyHolder);
		od.setId(Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d")));

	}

	private static final String commentsql = "insert into "
			+ oracle_comment_table
			+ " "
			+ "("
			+ "info, label, lv, person, product_code, product_title, pubtime, score, inserttime, cid, searchkey, product_url"
			+ ")" + "values " + "(?,?,?,?,?,?,?,?,?,?,?,?)";

	/**
	 * @param cd
	 * @param args
	 *            : 0: product code 1: searchkey 2: product url
	 */
	public void saveCommentOra(final CommentData cd, final String... args) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(commentsql, new String[] { "id" });
				// info, label, lv, person, product_code, product_title,
				// pubtime, score
				ps.setString(1, cd.getComment_info());
				ps.setString(2, cd.getComment_label());
				ps.setString(3, cd.getComment_level());
				ps.setString(4, cd.getComment_person());
				ps.setLong(5, Long.parseLong(args[0]));
				ps.setString(6, cd.getComment_product());
				try {
					Date date = sdf.parse(cd.getComment_pubtime().replace("年", "-").replace("月", "-").replace("日", ""));
					ps.setTimestamp(7, new Timestamp(date.getTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				ps.setString(8, cd.getComment_score());
				ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
				ps.setString(10, cd.getComment_id());
				ps.setString(11, args[1]);
				ps.setString(12, args[2]);
				return ps;
			}
		}, keyHolder);

		cd.setId(Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d")));
	}

	/*
	 * 插入一行数据
	 */
	@Override
	public void saveData(EbusinessData data) throws IOException {

		saveDataOra(data);
		if (data.getOwner() != null){			
			List<OwnerData> list=new ArrayList<OwnerData>();
			list.add(data.getOwner());
			Systemconfig.dbService.getNorepeatData(list, "");
			saveOwnerOra(data.getOwner(), data.getSearchKey(), data.getUrl());
		}
		if (data.getComments() != null) {
			List<CommentData> list=data.getComments();			
			Systemconfig.dbService.getNorepeatData(list, "");
			for (CommentData cd : list) {
				saveCommentOra(cd, data.getInfo_code(), data.getSearchKey(), data.getUrl());
			}
		}

		ht = new HTable(product_table.getBytes(), connection);// 表名
		String rowKey = "0_" + data.getInfo_code();
		Put put = new Put(Bytes.toBytes(rowKey));// 行键
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

		for (String field : productsFields) {
			if (field.equals("price") || field.equals("transation")) {
				field += "_" + sdf.format(new Date());
			}
			common(data, field, put);
		}
		for (String field : superFields) {
			common(data, field, put);
		}
		try {
			ht.put(put);
		} catch (Exception e) {
			e.printStackTrace();

		}

		saveData(data.getOwner(), "owner");
	}

	private void common(Object data, String field, Put put) {
		Object obj = null;
		Method method = null;
		String bak = field;
		try {
			if (field.contains("price_") || field.contains("transation_")) {
				field = field.split("_")[0];
			}
			method = EbusinessData.class.getMethod(methodName(field));
			obj = method.invoke(data);
			field = bak;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		// 写产品基本数据
		String qualifier = field;
		if (field.indexOf("_") > -1) {
			String tmp = field;
			field = tmp.split("_")[0];
			qualifier = tmp.split("_")[1];
		}
		if (obj instanceof String) {
			put.add(Bytes.toBytes(field), Bytes.toBytes(qualifier), Bytes.toBytes((String) obj));
		} else if (obj instanceof Integer) {
			put.add(Bytes.toBytes(field), Bytes.toBytes(qualifier), Bytes.toBytes((Integer) obj));
		} else if (obj instanceof Date) {
			put.add(Bytes.toBytes(field), Bytes.toBytes(qualifier), Bytes.toBytes(((Date) obj).getTime()));
		}
		if (obj instanceof List<?>) {
			// 写评论数据
			for (int i = 0; i < ((List<?>) obj).size(); i++) {// 第i条评论
				Put put1 = null;
				try {
					ht = new HTable(product_table.getBytes(), connection);
					String rowkey = "1" + new String(put.getRow()).replace("0", "")
							+ new String(((CommentData) ((List<?>) obj).get(i)).getComment_id());
					put1 = new Put(Bytes.toBytes(rowkey));
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				CommentData cd = (CommentData) ((List<?>) obj).get(i);
				for (int j = 0; j < commentFields.length; j++) {
					try {
						Method method1 = CommentData.class.getMethod(methodName(commentFields[j]));
						Object obj1 = method1.invoke(cd);
						if (obj1 instanceof String) {
							put1.add(Bytes.toBytes("comments"), Bytes.toBytes(commentFields[j]),
									Bytes.toBytes((String) obj1));
						} else if (obj1 instanceof Integer) {
							put1.add(Bytes.toBytes("comments"), Bytes.toBytes(commentFields[j]),
									Bytes.toBytes((Integer) obj1));
						} else if (obj1 instanceof Date) {
							put1.add(Bytes.toBytes("comments"), Bytes.toBytes(commentFields[j]),
									Bytes.toBytes(((Date) obj1).getTime()));
						}
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					try {
						ht.put(put1);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}// end for
			}
		}
		// 写卖家数据
		if (obj instanceof OwnerData) {
			attrSet(put, (OwnerData) obj, "owner");
		}
	}

	//
	// private void commonOwner(Object data, String field, Put put) {
	// // createTablesIfNotExist();
	// Object obj = null;
	// Method method = null;
	// try {
	// method = OwnerData.class.getMethod(methodName(field));
	// obj = method.invoke(data);
	// } catch (NoSuchMethodException e) {
	// e.printStackTrace();
	// } catch (SecurityException e) {
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// e.printStackTrace();
	// } catch (IllegalArgumentException e) {
	// e.printStackTrace();
	// } catch (InvocationTargetException e) {
	// e.printStackTrace();
	// }
	// String qualifier = field;
	// // if(field.contains("_"))
	// // System.out.println("?");
	// if (field.indexOf("_") > -1) {
	// String tmp = field;
	// field = tmp.split("_")[0];
	// qualifier = tmp.split("_")[1];
	// // qualifier = field.substring(field.indexOf("_") + 1);
	// }
	// if (obj instanceof String) {
	// put.add(Bytes.toBytes(field), Bytes.toBytes(qualifier),
	// Bytes.toBytes((String) obj));
	// } else if (obj instanceof Integer) {
	// put.add(Bytes.toBytes(field), Bytes.toBytes(qualifier),
	// Bytes.toBytes((Integer) obj));
	// } else if (obj instanceof Date) {
	// put.add(Bytes.toBytes(field), Bytes.toBytes(qualifier),
	// Bytes.toBytes(((Date) obj).getTime()));
	// }
	// }

	/**
	 * 向owner表插入数据
	 * 
	 * @param data
	 * @param field
	 * @throws IOException
	 */
	private void saveData(OwnerData data, String field) throws IOException {
		ht = new HTable(conf, owner_table);
		Put put = new Put(Bytes.toBytes(data.getOwner_code() + "_" + data.getOwner_product()));
		attrSet(put, data, field);
		ht.put(put);
	}

	private void attrSet(Put put, OwnerData data, String field) {
		Method method = null;

		for (String qualifier : ownersFields) {
			Object obj = null;
			try {
				method = OwnerData.class.getMethod(methodName(qualifier));// ebusinessdata改成ownerdata
				obj = method.invoke(data);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if (obj instanceof String) {
				put.add(Bytes.toBytes(field), Bytes.toBytes(qualifier), Bytes.toBytes((String) obj));
			} else if (obj instanceof Integer) {
				put.add(Bytes.toBytes(field), Bytes.toBytes(qualifier), Bytes.toBytes((Integer) obj));
			} else if (obj instanceof Date) {
				put.add(Bytes.toBytes(field), Bytes.toBytes(qualifier), Bytes.toBytes(((Date) obj).getTime()));
			}
		}
	}

	/*
	 * 反射方法名
	 */
	private static String methodName(String key) {
		return "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
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
			return;
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
			return;
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

}
