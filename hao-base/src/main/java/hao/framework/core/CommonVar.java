package hao.framework.core;


/***
 * 系统常量定义
 * @author chianghao
 *
 */
public class CommonVar {

	/***
	 * 枚举  json类型  
	 * json,jsonp
	 * @author chianghao
	 */
	public enum JSON_TYPE{
		
		json("json"),jsonp("jsonp");
		
		private String name;
		
		
		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}


		private JSON_TYPE(String name) {
			this.name = name;
		}
	}
	
	
	
	/***
	 * 枚举 
     * 系统消息常量
	 * @author chianghao
	 */
	public enum SystemMsg{
		
		error_no_seq_server("NO_SEQ_SERVER","没有序列服务，请检查是否启动！"),
		error_system_exception("HAO_FRAME_EXCEPTION","系统异常！"), 
		sql_build_no_entity("SQL_BUILD_NO_FIND_ENTITY","构建sql语句出错，没有找到对应的entity对象"),
		sql_builder_no_id("SQL_BUILD_PARAM_ID_IS_NO","构建sql语句出错，没有传递主键值"),
		sql_builder_update_fields_null("SQL_BUILD_UPDATE_FIELD_NULL","构建sql语句出错:构建update时,参数fields为null或者size小于等于零"),
		auth_no_find_auth_type("NO_FIND_AUTH_TYPE","账号认证错误:没有找到对应的认证方式,请查询系统配置文件有无配置认证方式"),
		auth_create_token_no_access("AUTH_CREATE_TOKEN_NO_ACCESS","创建登录钥匙错误，必须包含账号、手机、邮箱任何一个！"),
		auth_create_token_no_password("AUTH_CREATE_TOKEN_NO_PASSWORD","创建登录钥匙错误，必须包含账号、手机、邮箱任何一个！"),
		auth_query_password_no_conf_access_column("AUTH_QUERY_PASSWORD_NO_CONF_ACCESS_COLUMN","认证错误，查询密码过程中未能在配置文件中找到账号字段"),
		auth_no_find_access("AUTH_NO_FIND_ACCESS","认证错误未找到账号！"), 
		auth_error("AUTH_ERROR","账号认证错误！"), 
		dml_create_table_no_column("DML_CREATE_TABLE_NO_COLUMN","创建表时数据库不存在字段！"),
		no_find_entity("NO_FIND_ENTITY","未发现实体对象"),
		validate_error("VALIDATE_ERROR","验证异常信息"), 
		validate_field_not_column("VALIDATE_FIELD_NOT_COLUMN","被验证的字段不是数据库字段"), 
		validate_create_fail("VALIDATE_CREATE_FAIL","创建验证对象失败！"), 
		sub_form_not_exist_class("SUB_FORM_NOT_EXIST_CLASS","表单中不包含此类的操作"), 
		sub_form_change_data("SUB_FORM_CHANGE_DATA","表单操作中，装换数据类型出错"), 
		auth_no_find_check_method("AUTH_NO_FIND_CHECK_METHOD","未发现校验方法!"), 
		sql_build_entity_has_no_db_column("SQL_BUILD_ENTITY_HAS_NO_DB_COLUMN","entity对象中不存在数据库字段")
		;
		
		private SystemMsg(String code,String msg) {
			this.code = code;
			this.msg = msg;
		}
		
		private String code;
		private String msg;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		
		
	}
	
}
