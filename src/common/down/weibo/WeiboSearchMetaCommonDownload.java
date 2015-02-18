package common.down.weibo;

import java.io.File;
import java.io.IOException;
import java.rmi.server.Skeleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import common.bean.HtmlInfo;
import common.bean.WeiboData;
import common.down.GenericMetaCommonDownload;
import common.rmi.packet.SearchKey;
import common.rmi.packet.ViewInfo;
import common.rmi.packet.ViewInfo.InnerInfo;
import common.system.Systemconfig;
import common.system.UserAttr;
import common.system.UserManager;
import common.util.TimeUtil;

public class WeiboSearchMetaCommonDownload extends GenericMetaCommonDownload<WeiboData> {

	public WeiboSearchMetaCommonDownload(SearchKey key) {
		super(key);
	}

	private UserAttr userAttr;

	@Override
	public void prePorcess() {
		InnerInfo ii = null;
		if (Systemconfig.clientinfo != null) {
			ViewInfo vi = Systemconfig.clientinfo.getViewinfos().get(Systemconfig.localAddress + "_" + siteFlag);
			ii = vi.getCrawlers().get(key.getKey());
			ii.setAlive(1);
		}
		if (!siteinfo.getLogin())
			return;

		// 每次保证只有有效用户个执行，某个任务完成后，等待的下一个任务开始执行
		UserAttr ua = UserManager.getUser(siteFlag);
		while (ua == null) {
			Systemconfig.sysLog.log("暂时没有可用账号用于采集，等待账号中……");
			TimeUtil.rest(10);
			ua = UserManager.getUser(siteFlag);
		}
		userAttr = ua;
		if (!userAttr.getHadRun()) {
			http.monitorLogin(userAttr);
			ua.setHadRun(true);
			System.out.println("监测用户！！！" + userAttr.getName());
		}
		Systemconfig.sysLog.log("用户" + userAttr.getName() + "使用中！");
		if (ii != null) {
			ii.setAccountId(ua.getId());
			ii.setAccount(ua.getName());
			ii.setAccountTip("账号使用中！");
		}
	}

	@Override
	public void process() {
		List<WeiboData> alllist = new ArrayList<WeiboData>();
		List<WeiboData> list = new ArrayList<WeiboData>();
		String url = getRealUrl(siteinfo, gloaburl);
		int page = getRealPage(siteinfo);
		String keyword = key.getKey();
		map.put(keyword, 1);
		String nexturl = url;

		HtmlInfo html = htmlInfo("META");
		try {
			int totalCount = 0;
			while (nexturl != null && !nexturl.equals("")) {
				list.clear();

				html.setOrignUrl(nexturl);

				try {
					http.getContent(html, userAttr);
					// html.setContent(common.util.StringUtil.getContent("filedown/META/baidu/37b30f2108ed06501ad6a769ca8cedc8.htm"));

					nexturl = xpath
							.templateListPage(list, html, map.get(keyword), keyword, nexturl, key.getRole() + "");

					if (list.size() == 0) {
						Systemconfig.sysLog.log(url + "元数据页面解析为空！！");
						TimeUtil.rest(siteinfo.getDownInterval());
						break;
					}
					Systemconfig.sysLog.log(url + "元数据页面解析完成。");
					totalCount += list.size();
					Systemconfig.dbService.getNorepeatData(list, "");
					if (list.size() == 0) {
						Systemconfig.sysLog.log("无新数据。");
						TimeUtil.rest(siteinfo.getDownInterval());
						break;
					}
					alllist.addAll(list);

					map.put(keyword, map.get(keyword) + 1);
					if (map.get(keyword) > page)
						break;
					url = nexturl;
					if (nexturl != null)
						TimeUtil.rest(siteinfo.getDownInterval());

				} catch (Exception e) {
					e.printStackTrace();
					try {
						Systemconfig.dbService.saveLog(siteFlag, key, 3, url + "\r\n" + e.getMessage());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}// end while

			try {
				if (alllist.size() != 0) {
					Systemconfig.dbService.saveLog(siteFlag, key, 2, totalCount + "", alllist.size() + "");
					Systemconfig.dbService.saveDatas(alllist);
					Systemconfig.sysLog.log(keyword + "所有新数据已保存。" + alllist.size());
				} else {
					Systemconfig.sysLog.log(keyword + "已扫描结束，无新数据。");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			// 根据需要采集转发评论
			process(alllist);
		} finally {
			UserManager.releaseUser(siteFlag, userAttr);
		}
	}

	private ExecutorService comes = Executors.newFixedThreadPool(5);
	private ExecutorService rttes = Executors.newFixedThreadPool(5);

	private void process(List<WeiboData> list) {

		for (WeiboData wd : list) {
			if (wd.getCommentNum() > 0) {
				key.setKey(wd.getCommentUrl());
				Future<?> com = comes.submit(new WeiboCommentDownload(key, wd.getId(), userAttr));
				try {
					com.get();
				} catch (InterruptedException e) {
					com.cancel(true);
					try {
						Systemconfig.dbService.saveLog(siteFlag, key, 3, key.getKey() + "\r\n" + e.getMessage());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} catch (ExecutionException e) {
					com.cancel(true);
					try {
						Systemconfig.dbService.saveLog(siteFlag, key, 3, key.getKey() + "\r\n" + e.getMessage());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

			if (wd.getRttNum() > 0) {
				key.setKey(wd.getRttUrl());
				Future<?> rtt = rttes.submit(new WeiboRttDownload(key, wd.getId(), userAttr));
				try {
					rtt.get();
				} catch (InterruptedException e) {
					try {
						Systemconfig.dbService.saveLog(siteFlag, key, 3, key.getKey() + "\r\n" + e.getMessage());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					rtt.cancel(true);
				} catch (ExecutionException e) {
					try {
						Systemconfig.dbService.saveLog(siteFlag, key, 3, key.getKey() + "\r\n" + e.getMessage());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					rtt.cancel(true);
				}
			}
		}

	}

	private void processUser(List<WeiboData> list) {
		HtmlInfo html = new HtmlInfo();
		html.setSite(siteFlag);
		html.setEncode("utf-8");
		html.setType("USER" + File.separator + siteFlag);
		HashMap<String, String> urls = new HashMap<String, String>();
		for (WeiboData wd : list) {
			if (wd.getUid() == null)
				continue;
			html.setOrignUrl("http://weibo.com/aj/user/newcard?usercardkey=weibo_mp&type=1&id=" + wd.getUid());
			if (urls.containsKey(html.getOrignUrl())) {
				wd.setAddress(urls.get(html.getOrignUrl()));
			} else {
				try {
					http.getContent(html);
					// xpath.templateJsonUser(html, wd);
					urls.put(html.getOrignUrl(), wd.getAddress());
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Systemconfig.dbService.saveLog(siteFlag, key, 3, html.getOrignUrl() + "\r\n" + e.getMessage());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				TimeUtil.rest(5);
			}
		}
	}

}
