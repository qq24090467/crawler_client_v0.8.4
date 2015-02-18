数据爬虫，可作为独立爬虫或分布式爬虫的client，集成了新闻、论坛、博客、微博、视频、电商、微信、学术、会议、基金、专利、电子报等搜索和垂直采集功能，
根据crawlerType配置调整爬虫为某种类型
config.properties中crawler_type对应的采集类型:新闻-1,2，论坛-3,4，博客-5,6，微博-7,8，视频-9,10，学术-11,12，电商-13,14. 奇数搜索，偶数垂直
注：
	页面内容抽取包结构：
	extractor->AbstracExtractor->Xpath->News,Blog,Bbs……
							   ->Regex
							   ->other
	新增不同的采集类型时，在down、extractor包建相应的包，可参考已有格式，同时需要在service包建相应类型的数据库操作
							   
	数据存储：
	service->Mysql->News,blog,Bbs,Weibo,Video……
		   ->Oracle->News,blog,Bbs,Weibo,Video……
		   ->Hbase->Ebusiness……
		   ->other
	新增数据库操作时，在service包新增相应的包，可参考已有格式	   
	   
配置：
	config.properties
		distribute属性表示使用分布式或单机模式
		crawler_type表示采集类型，类型参见CrawlerType类
		
		
	抽取配置读取的两种形式：读取文件、读取数据库
	config/site文件夹中的文件表示某采集类型公用模板：
		bbs_search.xml	论坛搜索模板
		blog_search.xml	博客搜索模板
		news_search.xml	新闻搜索模板
		weibo_search.xml	微博搜索模板
	
	site文件夹中的文件表示为某站点的属性和Xpath配置
		文件名规范:采集类型+站点名，如bbs_search_tianya.xml

v0.8.2 
	调整抽取类的代码结构
	修复多个bug
	添加各爬虫的垂直采集
	增加配置读取方式
v0.8.3 
	*调整实现分布式爬虫控制功能
	调整分布式采集任务运行方式
	*修改数据库读取配置的处理
	新增默认下载和抽取类
	增加多个爬虫类型	
	*新增报告pdf下载
	*调整数据库数据获取方式
	*快照上传功能的可配置