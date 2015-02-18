package common.system;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class SiteTemplateAttr implements Serializable {

	private int id;
	private String templateName;
//	private int type;// 搜索，垂直
//	private int media;// 媒体类型
	private Timestamp lastModified;// 最后修改时间
	private String content;// 内容
	private String siteFlag;//站点标识

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}


	public Timestamp getLastModified() {
		return lastModified;
	}

	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSiteFlag() {
		return siteFlag;
	}

	public void setSiteFlag(String siteFlag) {
		this.siteFlag = siteFlag;
	}
	
}
