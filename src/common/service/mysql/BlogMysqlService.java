package common.service.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.BlogData;
import common.util.StringUtil;

public class BlogMysqlService extends MysqlService<BlogData> {

	private static final String TABLE = "blog_data";

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
			"img_url) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public void saveData(final BlogData vd) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(jasql, new String[]{"id"});
				ps.setString(1, vd.getTitle());
				ps.setString(2, vd.getBlogAuthor());
				ps.setTimestamp(3, vd.getPubdate()==null? new Timestamp(0):new Timestamp(vd.getPubdate().getTime()));
				ps.setString(4, vd.getSource());
				ps.setString(5, vd.getUrl());
				ps.setTimestamp(6, new Timestamp(vd.getInserttime().getTime()));
				ps.setString(7, vd.getSearchKey());
				ps.setInt(8, vd.getCategoryCode());//vd.getCategoryCode()
				ps.setString(9, vd.getMd5());
				ps.setString(10, vd.getContent()==null?"" : vd.getContent());
				ps.setString(11, vd.getBrief());
				ps.setInt(12, vd.getSiteId());
				ps.setString(13, vd.getImgUrl());
				return ps;
			}
		}, keyHolder);
		vd.setId(Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d")));
	}
	
}
