package common.service.oracle;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.hadoop.mapred.job_005fauthorization_005ferror_jsp;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

import common.bean.BBSData;
import common.bean.ReplyData;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.system.Job;
import common.system.Systemconfig;
import common.util.StringUtil;

public class BbsOracleService extends OracleService<BBSData> {


	

	private static final String COMM = "bbs_data_comment";
	private static final String DATA = "bbs_data";

	private static final String csql = "insert into " + COMM + "(" + "md5," + "name," + "pubtime," + "insert_time,"
			+ "content," + "img_url," + "data_id) values(?,?,?,?,?,?,?)";

	public void saveDatas(List<ReplyData> list, int id) {
		for (ReplyData vd : list)
			saveComment(vd, id);
	}

	private void saveComment(final ReplyData vd, final int refer) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(csql, new String[] { "id" });
				ps.setString(1, vd.getMd5());
				ps.setString(2, vd.getName());
				ps.setObject(3, new Timestamp(vd.getPubdate().getTime()));
				ps.setObject(4, new Timestamp(System.currentTimeMillis()));
				ps.setString(5, vd.getContent());
				ps.setString(6, vd.getImgUrl());
				ps.setInt(7, refer);
				return ps;
			}
		}, keyHolder);
	}

	private static final String vsql = "insert into " + DATA + "(" + "url, " + "md5," + "title," + "author," + "brief,"
			+ "insert_time," + "content," + "comment_count," + "click_count," + "search_keyword," + "site_id,"
			+ "img_url," + "pubtime," + "category_code) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public void saveData(final BBSData vd) {
		// if(findId(vd.getMd5(), DATA)>0) return;

		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(vsql, new String[] { "id" });
				ps.setString(1, vd.getUrl());
				ps.setString(2, vd.getMd5());
				ps.setString(3, vd.getTitle());
				ps.setString(4, vd.getAuthor());
				ps.setString(5, vd.getBrief());
				ps.setObject(6, new Timestamp(System.currentTimeMillis()));
				ps.setString(7, vd.getContent());
				ps.setInt(8, vd.getReplyCount());
				ps.setInt(9, vd.getClickCount());
				ps.setString(10, vd.getSearchKey());
				ps.setInt(11, vd.getSiteId());
				ps.setString(12, vd.getImgUrl());
				ps.setObject(13, new Timestamp(vd.getPubdate().getTime()));
				ps.setInt(14, vd.getCategoryCode());
				return ps;
			}
		}, keyHolder);

		vd.setId(Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d")));
		
		saveCommonData(vd);
	}

	public void saveCommonData(BBSData vd) {
		if (vd.getReplyList() != null) {
			for (ReplyData rd : vd.getReplyList()) {
				saveComment(rd, vd.getId());
			}
		}
	}

	// private int findId(String md5, String table) {
	// String col = "id";
	// String caluse = "md5";
	// String sql = "select "+col+" from "+table+" where "+caluse+"=?";
	// int id = 0;
	// try {
	// id = this.jdbcTemplate.queryForInt(sql, new Object[]{md5});
	// } catch (Exception e) {
	// id = 0;
	// }
	// return id;
	// }

}
