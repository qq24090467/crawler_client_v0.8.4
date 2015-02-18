package common.rmi.packet;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 工作线程状态监控,实时获得线程信息
 * @author grs
 * @since 2013.6
 */
@SuppressWarnings("serial")
public class TaskStatus implements Serializable {

	/**是否完成*/
	protected boolean done;
	/**任务名*/
	protected String name;
	/**状态码*/
	protected Status status = Status.INTIAL;
	/** 启动时间*/
	protected long startTime;
	/**结束时间*/
	protected long endTime;
	/**当前任务量*/
	protected int taskNum;
	/** 已下载的页数 */
	protected int pageDownNum;
	/**总下载数量*/
	protected int allDownNum;
	/**不重复数量*/
	protected int noRepeatDownNum;
	/** 时间戳 */
	protected long heartTime;
	/**失败采集连接*/
	protected ConcurrentHashMap<String, ExceptionCode> failUrls = new ConcurrentHashMap<String,ExceptionCode>();
	
	public TaskStatus(String name) {
		this.name = name;
		heartTime = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String workName) {
		this.name = workName;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public int getPageDownNum() {
		return pageDownNum;
	}
	public void setPageDownNum(int pageDownNum) {
		this.pageDownNum = pageDownNum;
	}
	public int getAllDownNum() {
		return allDownNum;
	}
	public void setAllDownNum(int allDownNum) {
		this.allDownNum = allDownNum;
	}
	public int getNoRepeatDownNum() {
		return noRepeatDownNum;
	}
	public void setNoRepeatDownNum(int noRepeatDownNum) {
		this.noRepeatDownNum = noRepeatDownNum;
	}
	public ConcurrentHashMap<String, ExceptionCode> getFailUrls() {
		return failUrls;
	}
	public void setFailUrls(ConcurrentHashMap<String, ExceptionCode> failUrls) {
		this.failUrls = failUrls;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return startTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
		if(done)
			endTime = System.currentTimeMillis();
	}
	public int getTaskNum() {
		return taskNum;
	}
	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}
	public long getHeartTime() {
		return heartTime;
	}
	public void setHeartTime(long heartTime) {
		this.heartTime = heartTime;
	}
	/**
	 * 获得当前线程总运行时间
	 * @return 总运行时间<毫秒>
	 */
	public long getRunTime(){
		if(done)
			return endTime - startTime;
		return System.currentTimeMillis() - startTime;
	}
	
}
