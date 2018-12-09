package hao.framework.core.sequence;

import hao.framework.core.expression.HaoException;

public class Seq {

	private static SnowflakeIdWorker snowflakeInWorker = null;
	
	/**
	 * 设置雪花算法服务
	 */
	public static void setSnowflakeIdWorker() {
		snowflakeInWorker = new SnowflakeIdWorker(1,1);
	}
	/***
	 * 获取下一个主键
	 * @return
	 * @throws HaoException 
	 */
	public static long getNextId(){
		if(snowflakeInWorker==null) {
			return 0;
		}
		long id = snowflakeInWorker.nextId();
		return id;
//		if(snowflakeInWorker==null) {
//			throw new HaoException(SystemMsg.error_no_seq_server.getCode(),SystemMsg.error_no_seq_server.getMsg());
//		}
//		try {
//			
//		}catch(Exception e) {
//			throw new HaoException(SystemMsg.error_system_exception.getCode(), e.getMessage());
//		}
	}
	
}
