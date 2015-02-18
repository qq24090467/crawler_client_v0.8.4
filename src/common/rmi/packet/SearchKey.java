package common.rmi.packet;

import java.io.Serializable;

public class SearchKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8592400718234618947L;
	private int id;// 词项ID
	private String name;// 垂直名，搜索无该属性
	private String site;// 站点标识
	private String key;// 关键词/网址
	private int type;// 类型
	private int role;// 角色
	private int person;// 用户
	private String ip;// 运行的IP
	private int savedCount;// 已入库的记录数

	public void savedCountIncrease() {
		this.savedCount++;
	}

	public int getSavedCount() {
		return savedCount;
	}

	public void setSavedCount(int savedCount) {
		this.savedCount = savedCount;
	}

	@Override
	public String toString() {
		return "[site]" + site + "[key]" + key;

	};

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String nameFlag) {
		this.site = nameFlag;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String url) {
		this.key = url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public int getPerson() {
		return person;
	}

	public void setPerson(int person) {
		this.person = person;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
