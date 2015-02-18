package common.down.weibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import common.bean.WeiboData;
import common.bean.HtmlInfo;
import common.down.GenericDataCommonDownload;
import common.rmi.packet.SearchKey;
import common.siteinfo.CollectDataType;
import common.siteinfo.Siteinfo;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.util.TimeUtil;

/**
 * 下载详细页面
 * 
 * @author grs
 */
public class WeiboDataCommonDownload extends GenericDataCommonDownload<WeiboData> {

	private UserAttr user;

	public WeiboDataCommonDownload(String siteFlag, WeiboData data, CountDownLatch count, UserAttr user, SearchKey key) {
		super(siteFlag, data, count, key);
		this.user = user;
	}

	@Override
	public void process() {
		List<WeiboData> alllist = new ArrayList<WeiboData>();
		List<WeiboData> list = new ArrayList<WeiboData>();
		Siteinfo siteinfo = Systemconfig.allSiteinfos.get(siteFlag);
		String url = data.getUrl();
		String nexturl = url;
		HtmlInfo html = htmlInfo(CollectDataType.DATA.name());
		int count = 1;
		try {
			while (nexturl != null && !nexturl.equals("")) {
				list.clear();

				html.setOrignUrl(nexturl);
				try {
					http.getContent(html, user);
					// html.setContent(common.util.StringUtil.getContent("filedown/DATA/sina_weibo_monitor/470787a06eb41ba38e27d45c1e40d0e4.htm"));

					nexturl = xpath.templateListPage(list, html, count, data.getId() + "", nexturl,
							data.getCategoryCode() + "");

					if (list.size() == 0) {
						Systemconfig.sysLog.log(url + "数据页面解析为空！！");
						break;
					}
					Systemconfig.sysLog.log(url + "数据页面解析完成。");

					Systemconfig.dbService.getNorepeatData(list, "weibo_data");

					alllist.addAll(list);

					if (alllist.size() >= 500) {
						Systemconfig.dbService.saveDatas(alllist);
						for (int i = 0; i < alllist.size(); i++)
							synchronized (key) {
								key.savedCountIncrease();
							}
						alllist.clear();
					}
					url = nexturl;
					if (url != null && !url.contains("&max_id="))
						count++;
					if (nexturl != null)
						TimeUtil.rest(siteinfo.getDownInterval());

				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			Systemconfig.dbService.saveDatas(alllist);
			for (int i = 0; i < alllist.size(); i++)
				synchronized (key) {
					key.savedCountIncrease();
				}
			alllist.clear();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				Systemconfig.dbService.saveLog(siteFlag, key, 3,  url+"\r\n"+e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			alllist.clear();
			list.clear();
		}
		// 根据需要采集转发评论
		// process(alllist);
	}

	private ExecutorService comes = Executors.newFixedThreadPool(5);
	private ExecutorService rttes = Executors.newFixedThreadPool(5);

	private void process(List<WeiboData> list) {

		for (WeiboData wd : list) {
			if (wd.getCommentNum() > 0) {
				key.setKey(wd.getCommentUrl());
				Future<?> com = comes.submit(new WeiboCommentDownload(key, wd.getId(), user));
				try {
					com.get();
				} catch (InterruptedException e) {
					com.cancel(true);
				} catch (ExecutionException e) {
					com.cancel(true);
				}
			}

			if (wd.getRttNum() > 0) {
				key.setKey(wd.getRttUrl());
				Future<?> rtt = rttes.submit(new WeiboRttDownload(key, wd.getId(), user));
				try {
					rtt.get();
				} catch (InterruptedException e) {
					rtt.cancel(true);
				} catch (ExecutionException e) {
					rtt.cancel(true);
				}
			}
		}

	}

}
