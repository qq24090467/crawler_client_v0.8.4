package common.bean;

/**
 * 微博垂直博主信息
 * @author grs
 *
 */
@SuppressWarnings("serial")
public class UserData extends CommonData {

	private String author;
	private String authorId;
	private String authorUrl;
	private String authorImg;
	private String weiboUrl;
	private String fansUrl;
	private String followUrl;
	private String infoUrl;
	private int fansNum;
	private int attentNum;
	private int weiboNum;
	private String certify;
	private String address;
	private String sex;
	private String tag;
	private String nick;
	private String company;
	private String birth;
	private String registTime;
	private String concact;
	private int personId;
	private int type;//博主类型，粉丝，关注还是博主本人
	
	public String getConcact() {
		return concact;
	}
	public void setConcact(String concact) {
		this.concact = concact;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthorId() {
		return authorId;
	}
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
	public String getAuthorUrl() {
		return authorUrl;
	}
	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	public String getWeiboUrl() {
		return weiboUrl;
	}
	public void setWeiboUrl(String weiboUrl) {
		this.weiboUrl = weiboUrl;
	}
	public String getFansUrl() {
		return fansUrl;
	}
	public void setFansUrl(String fansUrl) {
		this.fansUrl = fansUrl;
	}
	public String getFollowUrl() {
		return followUrl;
	}
	public void setFollowUrl(String followUrl) {
		this.followUrl = followUrl;
	}
	public String getInfoUrl() {
		return infoUrl;
	}
	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}
	public String getAuthorImg() {
		return authorImg;
	}
	public void setAuthorImg(String authorImg) {
		this.authorImg = authorImg;
	}
	public int getFansNum() {
		return fansNum;
	}
	public void setFansNum(int fansNum) {
		this.fansNum = fansNum;
	}
	public int getAttentNum() {
		return attentNum;
	}
	public void setAttentNum(int attentNum) {
		this.attentNum = attentNum;
	}
	public int getWeiboNum() {
		return weiboNum;
	}
	public void setWeiboNum(int weiboNum) {
		this.weiboNum = weiboNum;
	}
	public String getCertify() {
		return certify;
	}
	public void setCertify(String certify) {
		this.certify = certify;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public String getRegistTime() {
		return registTime;
	}
	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}
	public int getPersonId() {
		return personId;
	}
	public void setPersonId(int personId) {
		this.personId = personId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
