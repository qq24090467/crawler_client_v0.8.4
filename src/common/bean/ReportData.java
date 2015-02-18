package common.bean;

public class ReportData extends CommonData {
	private String title;
	private String url;
	private String path;
	private String pubtime;
	private String companyId;
	private int typeId;
	private String search_key;
	
	public int getTypeId(ReportData data){
		String title=data.getTitle();
		if(title.contains("半年度")){
			return 2;
		}
		else if (title.contains("年度")) {
			return 1;
		}
		else if (title.contains("一季度")) {
			return 3;
		}
		else if (title.contains("三季度")) {
			return 4;
		}
		else if ((title.contains("首次")&&title.contains("公开")&&title.contains("发行"))||(title.contains("首次")&&title.contains("公开")&&title.contains("募股"))||title.contains("上市公告")) {
			return 5;
		}
		else {
			return 6;
		}
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPubtime() {
		return pubtime;
	}
	public void setPubtime(String pubtime) {
		this.pubtime = pubtime;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getSearch_key() {
		return search_key;
	}
	public void setSearch_key(String search_key) {
		this.search_key = search_key;
	}
	
	
	
}
