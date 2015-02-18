package common.up2hdfs;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import common.system.Systemconfig;

/**
 * 上传文件
 * @author grs
 *
 */
public class PollLocalFiles implements Runnable {
	 //一次性最多上传5个文件夹内文件
	private static final ExecutorService exec = Executors.newFixedThreadPool(5);
	
	private String dir;
	public PollLocalFiles(String dir) {
		this.dir = dir;
	}
	
	@Override
	public void run() {
		upFile(dir);
	}
	/**
	 * 上传指定文件夹内的所有符合条件的文件
	 * 该文件夹为一线程
	 * @param dir
	 */
	public void upFile(String dir) {
		Systemconfig.sysLog.log("开始上传文件到HDFS……");
		List<String> pathlist = pathList(dir);
		if(pathlist==null || pathlist.size()==0) return;
		CountDownLatch count = new CountDownLatch(pathlist.size());
		Iterator<String> iter = pathlist.iterator();
		long start = System.currentTimeMillis();
		
		int fileCount = 0;
		while(iter.hasNext()) {
			String path = iter.next();
			synchronized (pathlist) {
				iter.remove();
			}
			try {
				File file = new File(path);
				List<String> list = new ArrayList<String>();
				recuse(file, list);
				if(list.size()==0) {
					//需要减去
					count.countDown();
					continue;
				}
				fileCount += list.size();
				Future<String> fu = exec.submit(new UpFile2HDFS(list, count));
				String url = fu.get();
				if(url!=null) {
					if(Systemconfig.delLoaclFile)
						delLocalFile(list);//删除本地文件
				}
			} catch (FileNotFoundException e) {
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} 
		}
		try {
			count.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println("总上传"+fileCount+"个文件到HDFS用时："+(System.currentTimeMillis()-start));
	}
	
	private List<String> pathList(String root) {
		List<String> list = new ArrayList<String>();
		File[] files = new File(root).listFiles(new FileFilter() {
			@Override
			public boolean accept(File name) {
				return !name.getName().startsWith(".");
			}
		});
		if(files==null)return list;
		for(File f : files) {
			if(f.isDirectory()) 
				list.add(f.getAbsolutePath());
		}
		return list;
	}
	
	private void delLocalFile(List<String> list) {
		for(String s : list) {
			File f = new File(s+".up");
			f.delete();
		}
	}
	
	private class UPFileFilter implements FileFilter {
		final int flag;
		public UPFileFilter(int flag) {
			this.flag = flag;
		}
		@Override
		public boolean accept(File name) {
			switch(flag) {
			default : 
			case 0 : return !name.getName().endsWith(".up") && !name.getName().startsWith(".");
			case 1 : return name.getName().endsWith(".up");
			}
		}
	}
	private void recuse(File file, List<String> list) throws IOException {
		File[] fs = file.listFiles(new UPFileFilter(0));
		for(File f : fs) {
			if(f.isDirectory()) {
				recuse(f, list);
			} else {
				list.add(f.getAbsolutePath());
			}
		}
	}

}
