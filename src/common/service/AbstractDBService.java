package common.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import common.bean.CommonData;
import common.rmi.packet.CrawlerType;
import common.rmi.packet.SearchKey;
import common.system.Job;
import common.system.SiteTemplateAttr;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.util.StringUtil;
import common.util.UserAgent;

public abstract class AbstractDBService<T> implements DBService<T> {

	protected JdbcTemplate jdbcTemplate;

	private static final String LOG_TABLE = "sys_log";
	private static final String updateSql = "insert into " + LOG_TABLE
			+ " (ip, name, type, time, content, status) values (?,?,?,?,?,?)";

	@Override
	public void saveLog(final String siteFlag, SearchKey sk, int logType, String... info) throws UnknownHostException {

		final String ip = InetAddress.getLocalHost().getHostAddress().toString();
		int type = -1;
		String content = "";
		int status = -1;
		switch (Systemconfig.crawlerType) {

		case 1:
			type = 12;
			break;
		case 2:
			type = 13;
			break;
		case 3:
			type = 14;
			break;
		case 4:
			type = 15;
			break;
		case 5:
			type = 16;
			break;
		case 6:
			type = 17;
			break;
		case 7:
			type = 18;
			break;
		case 8:
			type = 19;
			break;

		case 13:
			type = 20;
			break;
		case 14:
			type = 21;
			break;

		case 19:
			type = 22;
			break;
		case 20:
			type = 23;
			break;

		case 15:
			type = 24;
		case 16:
			type = 25;

		case 21:
			type = 22;
		case 22:
			type = 23;

		default:
			break;
		}
		// System.out.println(ip);

		switch (logType) {

		case 1: {
			content = "爬虫启动，关键词：{\r\n" + sk.toString() + "\r\n}";
			status = 0;
			break;
		}
		case 2: {
			content = "关键词[" + sk.getKey() + "]列表页检索数据[" + info[0] + "]，其中新数据[" + info[1] + "]条";
			status = 0;
			break;
		}
		case 3: {
			content = "关键词[" + sk.getKey() + "]异常：{\r\n" + info[0] + "\r\n}";
			status = 1;
			break;
		}
		case 4: {
			content = "关键词[" + sk.getKey() + "]采集完成, 入库数量[" + info[0] + "]条";
			status = 0;
			break;
		}
		default:
			break;
		}
		// 必须是final，真烦
		final int crawlerLogType = type;
		final String logContent = content;
		final int logStatus = status;
		KeyHolder keyHolder = new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(updateSql, new String[] { "id" });
				// (ip, name, type, time, content, status) values (?,?,?,?,?,?)
				ps.setString(1, ip);
				ps.setString(2, siteFlag);
				ps.setInt(3, crawlerLogType);
				ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				ps.setString(5, logContent);
				ps.setInt(6, logStatus);
				return ps;
			}
		}, keyHolder);
		int id = Integer.parseInt(StringUtil.extrator(keyHolder.getKeyList().get(0).toString(), "\\d"));
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public abstract List<SearchKey> searchKeys();

	@Override
	public void saveDatas(List<T> list) throws IOException {
		for (T t : list) {
			saveData(t);
		}
	}

	@Override
	public List<? extends CommonData> getNorepeatData(List<? extends CommonData> list, String table) {
		Iterator<? extends CommonData> iter = list.iterator();
		List<CommonData> repeatDatas = new ArrayList<CommonData>();
		while (iter.hasNext()) {
			CommonData cd = iter.next();
			if (!Systemconfig.urm.checkNoRepeat(cd.getMd5())) {
				iter.remove();
				repeatDatas.add(cd);
			}
		}
		return list;
	}

	@Override
	public void deleteReduplicationUrls(List<String> urlList, String table) {
		String sql = "select id from " + table + " where md5=?";
		String DELETE_SQL = "delete from " + table + " where id=?";
		Iterator<String> urlIter = urlList.iterator();
		while (urlIter.hasNext()) {
			String url = urlIter.next();
			synchronized (urlList) {
				urlIter.remove();
			}
			List<Integer> idList = this.jdbcTemplate.queryForList(sql, new Object[] { url }, Integer.class);
			if (idList.size() > 1) {
				for (int i = idList.size() - 2; i >= 0; i--) {// 只留一条数据
					this.jdbcTemplate.update(DELETE_SQL, new Object[] { idList.get(i) });
				}
			}
		}
	}

	@Override
	public int getAllMd5(String table, Map<String, List<String>> map) {
		int num = 0;
		String[] tabs = table.split(",");
		try {
			for (String t : tabs) {
				String SQL_WEB = "select md5 from " + t.replace("ebusiness", "eb");
				List<String> list = this.jdbcTemplate.queryForList(SQL_WEB, String.class);
				num += list.size();
				map.put(t, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}

	@Override
	public void exceptionData(String md5, String table) {
		String esql = "update " + table + " set fail_count=fail_count+1 where md5=?";
		this.jdbcTemplate.update(esql, new Object[] { md5 });
	}

	private static final String user_sql = "select u.name, u.pass, ss.siteflag, u.id from crawler_account u, site_template ss"
			+ " where u.site_id=ss.id and u.valid=1 and ss.siteflag=?";

	@Override
	public List<UserAttr> getLoginUsers(String site) {
		site = site.substring(0, site.indexOf("_"));
		final List<UserAttr> list = new ArrayList<UserAttr>();
		this.jdbcTemplate.query(user_sql, new Object[] { site }, new RowMapper<UserAttr>() {
			@Override
			public UserAttr mapRow(ResultSet rs, int i) throws SQLException {
				UserAttr ua = new UserAttr();
				ua.setName(rs.getString(1));
				ua.setPass(rs.getString(2));
				ua.setUsed(0);
				ua.setAgentIndex(UserAgent.getUserAgentIndex());
				ua.setUserAgent(UserAgent.getUserAgent(ua.getAgentIndex()));
				ua.setSiteFlag(rs.getString(3));
				ua.setId(rs.getInt(4));

				list.add(ua);

				return ua;
			}
		});
		return list;
	}

	@Override
	public Map<String, SiteTemplateAttr> getXpathConfig() {

		String sql = "select ID,SITEFLAG, TEMPLATE_CONTENT_SITE, TEMPLATE_LAST_MODIFIED from SITE_TEMPLATE where MEDIA="
				+ ((Systemconfig.crawlerType + 1) / 2) + " and TYPE=" + ((Systemconfig.crawlerType + 1) % 2);// 奇数搜索

		final Map<String, SiteTemplateAttr> list = new HashMap<String, SiteTemplateAttr>();

		this.jdbcTemplate.query(sql, new RowMapper<SiteTemplateAttr>() {
			public SiteTemplateAttr mapRow(ResultSet rs, int i) throws SQLException {
				SiteTemplateAttr sta = new SiteTemplateAttr();
				sta.setTemplateName(CrawlerType.getMap().get(Systemconfig.crawlerType).name().toLowerCase());
				sta.setSiteFlag(rs.getString("SITEFLAG"));
				// sta.setType(rs.getString("TYPE"));
				sta.setLastModified(rs.getTimestamp("TEMPLATE_LAST_MODIFIED"));
				// sta.setMedia(rs.getString("MEDIA"));
				sta.setContent(rs.getString("TEMPLATE_CONTENT_SITE"));
				sta.setId(rs.getInt("ID"));
				list.put(sta.getSiteFlag() + "_" + sta.getTemplateName(), sta);

				return sta;
			}
		});
		// @Override
		// public SiteTemplateAttr mapRow(ResultSet rs, int i)
		// throws SQLException {
		// SiteTemplateAttr sta=new SiteTemplateAttr();
		// sta.setTemplateName(CrawlerType.getMap().get(Systemconfig.crawlerType).name().toLowerCase());
		// sta.setSiteFlag(rs.getString("SITEFLAG"));
		// sta.setType(rs.getString("TYPE"));
		// sta.setLastModified(rs.getTimestamp("TEMPLATE_LAST_MODIFIED"));
		// sta.setMedia(rs.getString("MEDIA"));
		// sta.setContent(rs.getString("TEMPLATE_CONTENT_SITE"));
		//
		// return null;
		// }
		//

		return list;
	}

	@Override
	public String getTypeConfig() {

		String monitorOrSearch = Systemconfig.crawlerType % 2 == 0 ? "MONITOR_TEMPLATE" : "SEARCH_TEMPLATE";
		String sql = "SELECT " + monitorOrSearch + " from MEDIA_TYPE where id = "
				+ ((Systemconfig.crawlerType + 1) / 2);
		String result = this.jdbcTemplate.queryForObject(sql, String.class);
		return result;

	}

}
