package common.bean;

@SuppressWarnings("serial")
public class CommentData extends CommonData {
	private String comment_person;//评论人
	private String comment_level;//评论人级别
	private String comment_info;//评论内容
	private String comment_pubtime;//评论时间
	private String comment_label;//标签
	private String comment_product;//
	private String comment_score;//评分
	private String comment_id;//评论id

	public String getComment_person() {
		return comment_person;
	}

	public void setComment_person(String comment_person) {
		this.comment_person = comment_person;
	}

	public String getComment_info() {
		return comment_info;
	}

	public void setComment_info(String comment_info) {
		this.comment_info = comment_info;
	}

	public String getComment_pubtime() {
		return comment_pubtime;
	}

	public void setComment_pubtime(String comment_pubtime) {
		this.comment_pubtime = comment_pubtime;
	}

	public String getComment_label() {
		return comment_label;
	}

	public void setComment_label(String comment_label) {
		this.comment_label = comment_label;
	}

	public String getComment_product() {
		return comment_product;
	}

	public void setComment_product(String comment_product) {
		this.comment_product = comment_product;
	}

	public String getComment_level() {
		return comment_level;
	}

	public void setComment_level(String comment_level) {
		this.comment_level = comment_level;
	}

	public String getComment_score() {
		return comment_score;
	}

	public void setComment_score(String comment_score) {
		this.comment_score = comment_score;
	}

	public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}

}
