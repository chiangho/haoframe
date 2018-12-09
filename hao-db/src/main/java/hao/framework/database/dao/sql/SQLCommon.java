package hao.framework.database.dao.sql;

public class SQLCommon {

	/**
	 * join 符号
	 * @author chianghao
	 */
	public enum SqlJoinType{
		
		join("join"),
		left_join("left join"),
		inner_join("inner join"),
		right_join("right join")
		;
		
		private String type;
		
		private SqlJoinType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
	
	/***
	 * sql 语句的排序
	 * @author chianghao
	 */
	public enum SqlSort{
		
		asc("asc"),
		desc("desc");
		
		private String sort;
		
		private SqlSort(String sort) {
			this.sort = sort;
		}
		public String getSort() {
			return sort;
		}
		public void setSort(String sort) {
			this.sort = sort;
		}
	}
	
	/**
	 * sql 逻辑符号
	 * @author chianghao
	 */
	public enum SqlLogicalOperator{
		or("or"),
		and("and");
		private String operator;
		private SqlLogicalOperator(String operator) {
			this.operator = operator;
		}
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
	}
	
	/**
	 * sql 连接符号
	 * @author chianghao
	 */
	public enum SqlLinkOperator{
		or("or"),
		and("and"),
		orOpen("or ("),
		orClose(") or"),
		andOpen("and ("),
		andClose(") and"),
		close(")");
		private String operator;
		private SqlLinkOperator(String operator) {
			this.operator = operator;
		}
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
	}
	
	/**
	 * 操作符号
	 * @author chianghao
	 */
	public enum SqlOperator{
		greater(">"),
		greaterEqual(">="),
		equal("="),
		notEqual("!="),
		less("<"),
		lessEqual("<="),
		in("in"),
		notIn("not in"),
		between("between"),
		isNot("is not null"),
		like("like")
		;
		
		private String operator;
		private SqlOperator(String operator) {
			this.operator = operator;
		}
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
	}
	
}
