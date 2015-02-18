package common.rmi.packet;

/**
 * @author grs
 * @since 2013.6
 */
public enum Status {

	INTIAL,//等待开始
	RUNNING,//运行元数据
	DONE,//完成
	FAIL;//采集异常
}
