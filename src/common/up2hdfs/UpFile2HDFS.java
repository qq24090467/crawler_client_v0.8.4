package common.up2hdfs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import common.rmi.packet.CrawlerType;
import common.system.Systemconfig;

/**
 * 文件上传到HDFS
 * 写入时使用sequenceFile或MapFile速度更快
 * 20G用时54分钟, 4G用时731s，速度在1s上传6M左右
 * @author grs
 * 上传策略：本地文件夹中的文件超过2000个或文件大小超过500M或一次采集完成
 * 上传成功的文件重命名，加txt后缀
 * 建立一个文件记录下载文件的大小和数量和完成状态，每30分钟轮询该文件
 */
public class UpFile2HDFS implements Callable<String> {
	private static final ExecutorService exec = Executors.newFixedThreadPool(Systemconfig.upThreadNum);
	
	private final List<String> list;
	private final String remote;
	private final int buffer;
	private CountDownLatch dirCount;
	
	public UpFile2HDFS(List<String> list, CountDownLatch count) {
		this.list = list;
		this.remote = Systemconfig.remote;
		this.buffer = 4096;
		this.dirCount = count;
	}
	public UpFile2HDFS(List<String> list, String remote, CountDownLatch count) {
		this.list = list;
		this.remote = remote;
		this.buffer = 4096;
		this.dirCount = count;
	}
	public UpFile2HDFS(List<String> list, String remote, int buffer, CountDownLatch count) {
		this.list = list;
		this.remote = remote;
		this.buffer = buffer;
		this.dirCount = count;
	}

	@Override
	public String call() throws Exception {
		Configuration conf = new Configuration();
		CountDownLatch count = new CountDownLatch(list.size());
		Text key = new Text();
		Text val = new Text();
		SequenceFile.Writer writer = null;
		FileSystem fs = null;
		Path p = null;
		try {
			fs = FileSystem.get(URI.create(remote), conf);
			String[] arr = list.get(0).split(Systemconfig.filePath);
			
			if(arr.length ==2) {
				String s = arr[1].replace(File.separator, ",").substring(1);
				arr = s.split(",");
			}
			p = new Path(remote+File.separator+CrawlerType.getMap().get(Systemconfig.crawlerType)+
					File.separator+arr[0]+File.separator+File.separator+arr[1]+
					File.separator+Systemconfig.getClientIndex()+"_"+System.currentTimeMillis());
			
			writer = new SequenceFile.Writer(fs, new Configuration(), p, key.getClass(), val.getClass());
			long start = System.currentTimeMillis();
			for(String s : list) {
				exec.execute(new UpThread(writer, new File(s),buffer, count));
			}
			try {
				count.await();
			} catch(InterruptedException e) {
				e.printStackTrace();
				return null;
			}
			System.err.println(arr[1]+"上传"+list.size()+"个文件，用时:"+(System.currentTimeMillis()-start));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtils.closeStream(writer);
			dirCount.countDown();
		}
		return p.getName();
	}
	
	private class UpThread implements Runnable {
		
		private final File file;
		private int buffer;
		private SequenceFile.Writer writer;
		private CountDownLatch count;

		public UpThread(SequenceFile.Writer writer, File file,int bufferSize, CountDownLatch count) {
			this.writer = writer;
			this.file = file;
			this.buffer = bufferSize;
			this.count = count;
		}
		
		@Override
		public void run() {
			FileInputStream in = null;
			ByteArrayOutputStream fos = new ByteArrayOutputStream();
			Text key = new Text();
			Text val = new Text();
			try {
				if(file.canRead()) {
					in = new FileInputStream(file);
					
					key.set(file.getName());
					IOUtils.copyBytes(in, fos, buffer, true);
					val.set(new String(fos.toByteArray(), "utf-8"));
					writer.append(key, val);
					file.renameTo(new File(file.getAbsolutePath()+".up"));//重命名表示已上传
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeStream(in);
				IOUtils.closeStream(fos);
				count.countDown();
			}
		}
	}

}
