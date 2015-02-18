package common.util;

/**
 * content , image links
 * Used in HtmlExtractor
 * 
 * @see HtmlExtractor
 * @author Ahui Wang
 * 
 */
public class ExtractResult {
	private String title;
	private String content;
	private String imgs;

	public ExtractResult(String title, String content) {
		this.title = title;
		this.content = content;
	}
	public ExtractResult(String title, String content, String images) {
		this.title = title;
		this.content = content;
		this.imgs = images;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImgs() {
		return imgs;
	}
	public void setImgs(String imgs) {
		this.imgs = imgs;
	}
	
}