package common.bean;

@SuppressWarnings("serial")
public class AcademicData extends CommonData {
	private String brief;//摘要
	private String keywords;//文献关键词
	private String downurl;;//文献下载链接
	private String enAuthor;
	private String pubtime;//
	private String author;//作者
	private String referUrl;//引用链接
	private String citeUrl;//被引链接
	private int referNum;//引用次数
	private int citeNum;//被引次数
	private String downnum;
	private String email;//email
	private String fund;//基金
	private String category;//文献类别
	private String journal;//文献期刊
	private String address;//作者地址
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFund() {
		return fund;
	}
	public void setFund(String fund) {
		this.fund = fund;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getJournal() {
		return journal;
	}
	public void setJournal(String journal) {
		this.journal = journal;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getDownurl() {
		return downurl;
	}
	public void setDownurl(String downurl) {
		this.downurl = downurl;
	}
	public String getEnAuthor() {
		return enAuthor;
	}
	public void setEnAuthor(String enAuthor) {
		this.enAuthor = enAuthor;
	}
	public String getPubtime() {
		return pubtime;
	}
	public void setPubtime(String pubtime) {
		this.pubtime = pubtime;
	}
	public String getReferUrl() {
		return referUrl;
	}
	public void setReferUrl(String referUrl) {
		this.referUrl = referUrl;
	}
	public String getCiteUrl() {
		return citeUrl;
	}
	public void setCiteUrl(String citeUrl) {
		this.citeUrl = citeUrl;
	}
	public int getReferNum() {
		return referNum;
	}
	public void setReferNum(int referNum) {
		this.referNum = referNum;
	}
	public int getCiteNum() {
		return citeNum;
	}
	public void setCiteNum(int citeNum) {
		this.citeNum = citeNum;
	}
	public String getDownnum() {
		return downnum;
	}
	public void setDownnum(String downnum) {
		this.downnum = downnum;
	}
	
}
