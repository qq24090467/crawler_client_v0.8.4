package common.bean;

@SuppressWarnings("serial")
public class WeiboData extends CommonData {

	private String brief;
	private String pubtime;
	private String authorurl;
	private String author;
	private String source;
	private String imgUrl;
	private String authorImg;
	private String mid;
	private String commentUrl;
	private String rttUrl;
	private int commentNum;
	private int rttNum;
	private String uid;
	private String address;
	private int dataId;
	
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
	public String getAuthorurl() {
		return authorurl;
	}
	public void setAuthorurl(String authorurl) {
		this.authorurl = authorurl;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
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
	public String getAuthorImg() {
		return authorImg;
	}
	public void setAuthorImg(String authorImg) {
		this.authorImg = authorImg;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getCommentUrl() {
		return commentUrl;
	}
	public void setCommentUrl(String commentUrl) {
		this.commentUrl = commentUrl;
	}
	public String getRttUrl() {
		return rttUrl;
	}
	public void setRttUrl(String rttUrl) {
		this.rttUrl = rttUrl;
	}
	public int getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(int commentCount) {
		this.commentNum = commentCount;
	}
	public int getRttNum() {
		return rttNum;
	}
	public void setRttNum(int rttNum) {
		this.rttNum = rttNum;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setDataId(int dataId) {
		this.dataId = dataId;
	}
	public int getDataId() {
		return dataId;
	}
	
}
