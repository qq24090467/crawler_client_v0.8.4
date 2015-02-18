package common.service.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.NewsData;
import common.util.StringUtil;

public class NewsMysqlService extends MysqlService<NewsData> {

	private static final String TABLE = "news_data";

	private static final String jasql = "insert into "+TABLE+"(" +
			"title, " +
			"author," +
			"pubtime," +
			"source," +
			"url," +
			"inserttime," +
			"search_keyword," +
			"category_code," +
			"md5," +
			"content," +
			"brief," +
			"site_id," + 
			"img_url,"
			+ "same_num) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public void saveData(final NewsData vd) {
//		if(findId(vd.getMd5(), TABLE)>0) return;
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(jasql, new String[]{"id"});
				ps.setString(1, vd.getTitle());
				ps.setString(2, vd.getAuthor());
				ps.setObject(3, vd.getPubdate());
				ps.setString(4, vd.getSource());
				ps.setString(5, vd.getUrl());
				ps.setObject(6,vd.getInserttime());
				ps.setString(7, vd.getSearchKey());
				ps.setInt(8, 8);//vd.getCategoryCode()
				ps.setString(9, vd.getMd5());
				ps.setString(10, vd.getContent()==null?"" : vd.getContent());
				ps.setString(11, vd.getBrief());
				ps.setInt(12, vd.getSiteId());
				ps.setString(13, vd.getImgUrl());
				ps.setInt(14, vd.getSamenum());
				return ps;
			}
		}, keyHolder);
		vd.setId(Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d")));
	}
	
	private static final String SAME_TABLE = "news_data_same";
	private static final String samesql = "insert into "+SAME_TABLE+"(" +
			"md5," +
			"title," +
			"source," +
			"url," +
			"insert_time," +
			"pubtime," +
			"content," +
			"img_url," +
			"data_id) values(?,?,?,?,?,?,?,?,?)";
	/**
	 * 保存新闻中相同新闻数据
	 * @param data
	 */
	public void saveSameData(final NewsData data) {
//		if(findId(data.getMd5(), SAME_TABLE)>0) return;
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(samesql, new String[]{"id"});
				ps.setString(1, data.getMd5());
				ps.setString(2, data.getTitle());
				ps.setString(3, data.getSource());
				ps.setString(4, data.getUrl());
				ps.setTimestamp(5, new Timestamp(data.getInserttime().getTime()));
				ps.setTimestamp(6, new Timestamp(data.getPubdate().getTime()));
				ps.setString(7, data.getContent());
				ps.setString(8, data.getImgUrl());
				ps.setInt(9, data.getId());
				return ps;
			}
		}, keyHolder);
	}
//	private int findId(String md5, String table) {
//		String col = "id";
//		String caluse = "md5";
//		String sql = "select "+col+" from "+table+" where "+caluse+"=?";
//		int id = 0;
//		try {
//			id = this.jdbcTemplate.queryForInt(sql, new Object[]{md5});
//		} catch (Exception e) {
//			id = 0;
//		}
//		return id;
//	}

}
