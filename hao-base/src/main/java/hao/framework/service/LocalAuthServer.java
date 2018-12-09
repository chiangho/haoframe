package hao.framework.service;

import java.util.Vector;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import hao.framework.core.CommonVar.SystemMsg;
import hao.framework.core.HaoProperty;
import hao.framework.core.expression.HaoException;
import hao.framework.db.dao.jdbc.JdbcDao;
import hao.framework.web.auth.shiro.MemberToken;

/***
 * 本地认证服务
 * @author chianghao
 */
public class LocalAuthServer {

	@Autowired
	JdbcDao dao;

	public String queryPassword(MemberToken memberToken) throws HaoException {
		// TODO Auto-generated method stub
		Vector<String> params = new Vector<String>();
		//获取sql和参数
		String sql = getSql(params,memberToken);
		try {
			String password = dao.queryString(sql,params.toArray(new String[params.size()]));
			return password;
		}catch(Exception e) {
			throw new HaoException(SystemMsg.auth_no_find_access.getCode(), SystemMsg.auth_no_find_access.getMsg());
		}
	}
	
	/**
	 * 从配置文件中获取查询语句
	 * @return
	 * @throws HaoException 
	 */
	private String getSql(Vector<String> params,MemberToken memberToken) throws HaoException {
		String auth_user_table=HaoProperty.getValue("auth_user_table");
		String auth_password_column=HaoProperty.getValue("auth_password_column");
		String auth_access_columns=HaoProperty.getValue("auth_access_columns");//{"access":"access","mobilePhone":"mobile_phone","email":"e_mail"}
		
		JSONObject accessColumns = new JSONObject(auth_access_columns);
		String access_column = accessColumns.getString("access");
		String mobilPhone_colmun =  accessColumns.getString("mobilePhone");
		String email_colmun =  accessColumns.getString("email");
		
		if((access_column==null||access_column.equals(""))
				&&(mobilPhone_colmun==null||mobilPhone_colmun.equals(""))
				&&(email_colmun==null||email_colmun.equals(""))
				) {
			throw new HaoException(SystemMsg.auth_query_password_no_conf_access_column.getCode(), SystemMsg.auth_query_password_no_conf_access_column.getMsg());
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(" select "+auth_password_column+" from "+auth_user_table+" where ");
		
		String where ="";
		if(access_column!=null&&!access_column.equals("")) {
			if(memberToken.getUsername()!=null&&!memberToken.getUsername().equals("")) {
				where+="or "+access_column+"=? ";
				params.add(memberToken.getUsername());
			}
		}
		if(mobilPhone_colmun!=null&&!mobilPhone_colmun.equals("")) {
			if(memberToken.getMobilPhone()!=null&&!memberToken.getMobilPhone().equals("")) {
				where+="or "+mobilPhone_colmun+"=? ";
			}
		}
		if(email_colmun!=null&&!email_colmun.equals("")) {
			if(memberToken.getEmail()!=null&&!memberToken.getEmail().equals("")) {
				where+="or "+email_colmun+"=? ";
			}
		}
		
		if(where.startsWith("or")) {
			where = where.substring(2);
		}
		sb.append(where);
		return sb.toString();
	}
	
}
