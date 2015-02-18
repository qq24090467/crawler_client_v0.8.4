package common.service.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.UserData;
import common.bean.WeiboData;

public class WeiboMysqlService extends MysqlService<WeiboData>{

	private static final String DATA = "weibo_data";
	
	private static final String jasql = "insert into "+DATA+"(" +
			"search_keyword, " +
			"md5," +
			"content," +
			"author," +
			"author_url," +
			"insert_time," +
			"source," +
			"img_url," +
			"author_img," +
			"url," +
			"site_id," +
			"pubtime," +
			"rtt_count," +
			"comment_count," +
			"rtt_url," +
			"comment_url," +
			"mid," +
			"category_code) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public void saveData(final WeiboData vd) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(jasql, new String[]{"id"});
				ps.setString(1, vd.getSearchKey());
				ps.setString(2, vd.getMd5());
				ps.setString(3, vd.getContent());
				ps.setString(4, vd.getAuthor());
				ps.setString(5, vd.getAuthorurl());
				ps.setObject(6, vd.getInserttime());
				ps.setString(7, vd.getSource());
				ps.setString(8, vd.getImgUrl());
				ps.setString(9, vd.getAuthorImg());
				ps.setString(10, vd.getUrl());
				ps.setInt(11, vd.getSiteId());
				ps.setObject(12, vd.getPubdate());
				ps.setInt(13, vd.getRttNum());
				ps.setInt(14, vd.getCommentNum());
				ps.setString(15, vd.getRttUrl());
				ps.setString(16, vd.getCommentUrl());
				ps.setString(17, vd.getMid());
				ps.setInt(18, vd.getCategoryCode());
				return ps;
			}
		}, keyHolder);
		
	}
	private static String PERSON = "weibo_person";
	private static String FANS = "weibo_fans";
	private static String FOLLOW = "weibo_follow";
	private static final String userUpdate = " set fans_num=?,attent_num=?,weibo_num=?,tag=?,info=?,certify=?,author_img=? where md5=?";
	private static final String userRelationSave = "(" +
			"author, " +
			"author_url," +
			"author_img," +
			"md5," +
			"insert_time," +
			"fans_num," +
			"follow_num," +
			"weibo_num," +
			"certify," +
			"address," +
			"info," +
			"sex," +
			"tag," +
			"fans_url," +
			"follow_url," +
			"weibo_url," +
			"info_url," +
			"nick," +
			"company," +
			"regist_time," +
			"birth," +
			"concact," +
			"uid," +
			"category_code," + 
			"site_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String userSave = "(" +
			"author, " +
			"author_url," +
			"author_img," +
			"md5," +
			"insert_time," +
			"fans_num," +
			"follow_num," +
			"weibo_num," +
			"certify," +
			"address," +
			"info," +
			"sex," +
			"tag," +
			"fans_url," +
			"follow_url," +
			"weibo_url," +
			"info_url," +
			"nick," +
			"company," +
			"regist_time," +
			"birth," +
			"concact," +
			"uid," +
			"person_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public void saveUser(final UserData data) {
		String updatesql = null;
		String table = null;
		String saveSql = null;
		if(data.getType()==0) {
			table = PERSON;
			updatesql = "update "+PERSON+userUpdate;
			saveSql = "insert into "+FANS+userSave;
		} else if(data.getType()==1) {
			table = FANS;
			updatesql = "update "+FANS+userUpdate;
			saveSql = "insert into "+FANS+userRelationSave;
		} else if(data.getType()==2) {
			table = FOLLOW;
			updatesql = "update "+FOLLOW+userUpdate;
			saveSql = "insert into "+FOLLOW+userRelationSave;
		}
		if(findId(data.getMd5(), table)>0) {
			this.jdbcTemplate.update(updatesql, data.getFansNum(),data.getAttentNum(),data.getWeiboNum(),data.getTag(),data.getContent(),data.getCertify(),data.getAuthorImg(),data.getMd5());
			return;
		}
		final String sql = saveSql;
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
				ps.setString(1, data.getAuthor());
				ps.setString(2, data.getAuthorUrl());
				ps.setString(3, data.getAuthorImg());
				ps.setString(4, data.getMd5());
				ps.setObject(5, data.getInserttime());
				ps.setInt(6, data.getFansNum());
				ps.setInt(7, data.getAttentNum());
				ps.setInt(8, data.getWeiboNum());
				ps.setString(9, data.getCertify());
				ps.setString(10, data.getAddress());
				ps.setString(11, data.getContent());
				ps.setString(12, data.getSex());
				ps.setString(13, data.getTag());
				ps.setString(14, data.getFansUrl());
				ps.setString(15, data.getFollowUrl());
				ps.setString(16, data.getWeiboUrl());
				ps.setString(17, data.getInfoUrl());
				ps.setString(18, data.getNick());
				ps.setString(19, data.getCompany());
				ps.setString(20, data.getRegistTime());
				ps.setString(21, data.getBirth());
				ps.setString(22, data.getConcact());
				ps.setString(23, data.getAuthorId());
				if(data.getType()==0) {
					ps.setInt(24, data.getCategoryCode());
					ps.setInt(25, data.getSiteId());
				} else {
					ps.setInt(24, data.getPersonId());
				}
				
				return ps;
			}
		}, keyHolder);
	}
	
	private static final String comm_sql = "insert into weibo_data_comm(" +
			"author," +
			"author_url," +
			"author_img," +
			"md5," +
			"insert_time," +
			"content," +
			"pubtime," +
			"data_id) values(?,?,?,?,?,?,?,?)";
	
	private static final String rtt_sql = "insert into weibo_data_rtt(" +
			"author," +
			"author_url," +
			"author_img," +
			"md5," +
			"insert_time," +
			"content," +
			"pubtime," +
			"data_id," +
			"url) values(?,?,?,?,?,?,?,?,?)";
	public void saveInteractiveDatas(List<WeiboData> list) {
		for(WeiboData wd : list)
			saveInteractiveData(wd);
	}
	private void saveInteractiveData(final WeiboData data) {
		String sql = comm_sql;
		if(data.getUrl() != null) {
			sql = rtt_sql;
		}
		final String s = sql;
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(s, new String[]{"id"});
				ps.setString(1, data.getAuthor());
				ps.setString(2, data.getAuthorurl());
				ps.setString(3, data.getAuthorImg());
				ps.setString(4, data.getMd5());
				ps.setObject(5, data.getInserttime());
				ps.setString(6, data.getBrief());
				ps.setString(7, data.getPubtime());
				ps.setInt(8, data.getId());
				if(data.getUrl()!=null)
					ps.setString(9, data.getUrl());
				return ps;
			}
		}, keyHolder);
	}
	
	private int findId(String md5, String table) {
		String sql = "select id from "+table+" where md5=?";
		List<Integer> id = null;
		try {
			id = this.jdbcTemplate.queryForList(sql, Integer.class, md5);
		} catch (Exception e) {
			e.printStackTrace();
			id = new ArrayList<Integer>();
		}
		if(id.size()>0) {
			return id.get(0);
		}
		return 0;
	}

}
