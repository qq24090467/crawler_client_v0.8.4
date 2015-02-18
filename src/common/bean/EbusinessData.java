package common.bean;

import java.util.List;

@SuppressWarnings("serial")
public class EbusinessData extends CommonData {
	private String name;//商品名
	private String brand;//品牌
	private String price;//价格
	private String imgs_info;//图片-商品介绍
	private String imgs_product;//图片-商品
	private String transation;//销量（京东没有此数据）
	private String info_code;//商品码
	private String info_pubtime;//商品上市时间
	private String info_type;//商品类型
	private String params_params;//参数
	private String params_width;//胎面宽度
	private String params_diameter;//直径
	private String params_model;//型号
	private String company;//目标品牌

	
	
	
	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getParams_model() {
		return params_model;
	}

	public void setParams_model(String params_model) {
		this.params_model = params_model;
	}

	private String list;//包装清单
	private OwnerData owner;//卖家
	
	private String indexFlag_globle;//更新标志位:全局
	private String indexFlag_price;//更新标志位:价格
	private String indexFlag_transaction;//更新标志位:销量
	private String indexFlag_comments;//更新标识位:评论
	private String updateDate;//更新日期(采集日期的上一个月，精确到月份)
	
	public String getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	private List<CommentData> comments;//产品评论列表
	
		
	
	
	public String getIndexFlag_globle() {
		return indexFlag_globle;
	}

	public void setIndexFlag_globle(String indexFlag_globle) {
		this.indexFlag_globle = indexFlag_globle;
	}

	public String getIndexFlag_price() {
		return indexFlag_price;
	}

	public void setIndexFlag_price(String indexFlag_price) {
		this.indexFlag_price = indexFlag_price;
	}

	public String getIndexFlag_transaction() {
		return indexFlag_transaction;
	}

	public void setIndexFlag_transaction(String indexFlag_transaction) {
		this.indexFlag_transaction = indexFlag_transaction;
	}

	

	

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}


	public String getImgs_info() {
		return imgs_info;
	}

	public void setImgs_info(String imgs_info) {
		this.imgs_info = imgs_info;
	}

	public String getImgs_product() {
		return imgs_product;
	}

	public void setImgs_product(String imgs_product) {
		this.imgs_product = imgs_product;
	}

	public String getTransation() {
		return transation;
	}

	public void setTransation(String transation) {
		this.transation = transation;
	}


	public String getInfo_code() {
		return info_code;
	}

	public void setInfo_code(String info_code) {
		this.info_code = info_code;
	}

	public String getInfo_pubtime() {
		return info_pubtime;
	}

	public void setInfo_pubtime(String info_pubtime) {
		this.info_pubtime = info_pubtime;
	}

	public String getInfo_type() {
		return info_type;
	}

	public void setInfo_type(String info_type) {
		this.info_type = info_type;
	}
	

	public String getParams_params() {
		return params_params;
	}

	public void setParams_params(String params_params) {
		this.params_params = params_params;
	}

	public String getParams_width() {
		return params_width;
	}

	public void setParams_width(String params_width) {
		this.params_width = params_width;
	}

	public String getParams_diameter() {
		return params_diameter;
	}

	public void setParams_diameter(String params_diameter) {
		this.params_diameter = params_diameter;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public OwnerData getOwner() {
		return owner;
	}

	public void setOwner(OwnerData owner) {
		this.owner = owner;
	}

	public List<CommentData> getComments() {
		return comments;
	}

	public void setComments(List<CommentData> comments) {
		this.comments = comments;
	}


	public String getIndexFlag_comments() {
		return indexFlag_comments;
	}


	public void setIndexFlag_comments(String indexFlag_comments) {
		this.indexFlag_comments = indexFlag_comments;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
