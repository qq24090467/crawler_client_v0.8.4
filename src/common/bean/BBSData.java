package common.bean;

import java.util.List;

@SuppressWarnings("serial")
public class BBSData extends CommonData {

	private String author;
	private String brief;
	private String pubfrom;
	private int clickCount;
	private int replyCount;
	private String column;
	private String imgUrl;
	private List<ReplyData> replyList;
	private String pubtime;
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPubfrom() {
		return pubfrom;
	}
	public void setPubfrom(String pubfrom) {
		this.pubfrom = pubfrom;
	}
	public int getClickCount() {
		return clickCount;
	}
	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}
	public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public List<ReplyData> getReplyList() {
		return replyList;
	}
	public void setReplyList(List<ReplyData> replyList) {
		this.replyList = replyList;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public void setPubtime(String time) {
		this.pubtime = time;
	}
	public String getPubtime() {
		return pubtime;
	}
	
}
