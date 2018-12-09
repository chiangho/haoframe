package hao.framework.core.seq;

import hao.framework.core.expression.HaoException;

public class Sequence {
	private static SnowflakeIdWorker snowflakeInWorker = new SnowflakeIdWorker(1,1);
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
	}
	
	public static void main(String[] args) {
		for(int i=0;i<100;i++) {
			System.out.println(Sequence.getNextId());
		}
	}
}
