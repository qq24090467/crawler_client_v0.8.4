package common.bean;

@SuppressWarnings("serial")
public class BlogData extends CommonData {

	private String brief;
	private String source;
	private String pubtime;
	private String cacheurl;
	private String blogName;
	private String blogAuthor;
	private String imgUrl;
	
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
	public String getCacheurl() {
		return cacheurl;
	}
	public void setCacheurl(String cacheurl) {
		this.cacheurl = cacheurl;
	}
	public String getBlogName() {
		return blogName;
	}
	public void setBlogName(String blogName) {
		this.blogName = blogName;
	}
	public String getBlogAuthor() {
		return blogAuthor;
	}
	public void setBlogAuthor(String blogAuthor) {
		this.blogAuthor = blogAuthor;
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
	
}
