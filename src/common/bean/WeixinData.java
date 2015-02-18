package common.bean;

@SuppressWarnings("serial")
public class WeixinData extends CommonData{
	
	private String brief;
	private String pubtime;
//	private String cacheurl;
//	private String relnum;
	private String source;
	private String imgUrl;
	private String sameUrl;
	private String author;
	private int readNum;
	private int praiseNum;
	private int samenum;
	
	
	
	public int getReadNum() {
		return readNum;
	}
	public void setReadNum(int readNum) {
		this.readNum = readNum;
	}
	public int getPraiseNum() {
		return praiseNum;
	}
	public void setPraiseNum(int praiseNum) {
		this.praiseNum = praiseNum;
	}
	public String getSameUrl() {
		return sameUrl;
	}
	public void setSameUrl(String sameUrl) {
		this.sameUrl = sameUrl;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public String getPubtime() {
		return pubtime;
	}
	public void setPubtime(String pubtime) {
		this.pubtime = pubtime;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getSamenum() {
		return samenum;
	}
	public void setSamenum(int samenum) {
		this.samenum = samenum;
	}

}
