package common.bean;

@SuppressWarnings("serial")
public class VideoData extends CommonData {

	private String imgUrl;
	private int playCount;
	private String commentUrl;
	private String pubtime;
	private String tags;
	private String author;
	private String playtime;
	private String channel;
	
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String picUrl) {
		this.imgUrl = picUrl;
	}
	public int getPlayCount() {
		return playCount;
	}
	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}
	public String getCommentUrl() {
		return commentUrl;
	}
	public void setCommentUrl(String commentUrl) {
		this.commentUrl = commentUrl;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String keywords) {
		this.tags = keywords;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getPubtime() {
		return pubtime;
	}
	public void setPubtime(String pubtime) {
		this.pubtime = pubtime;
	}
	public String getPlaytime() {
		return playtime;
	}
	public void setPlaytime(String playtime) {
		this.playtime = playtime;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
}
