package hao.framework.web.jwt;


import hao.framework.core.spring.Property;
import hao.framework.core.utils.StringUtils;

/**
 * 单例模式，使用懒汉加载方式，生成jwt 相关的常量
 * @author chianghao
 *
 */
public class JwtConstants {
	
	private final static JwtConstants jwtConstants = new JwtConstants();
	
	//密钥
	private final String  JWT_SECRET;
	
	//http header key
	private final String  TOKEN_AUTHORIZATION;
	
	//token 失效时间
	private final long    TOKEN_OUT_TIME;

	/**
	 * 当前用户
	 */
	private final String  CURRENT_USER_NAME;
	
	private JwtConstants(){
		String JwtSecret = Property.getValue("jwt.secret");
		if(StringUtils.isEmpty(JwtSecret)) {
			JwtSecret = "JYJ5Qv2WF4lA6jPl5GKuAG";
		}
		this.JWT_SECRET = JwtSecret;
		
		String authorization = Property.getValue("jwt.token.authorization");
		if(StringUtils.isEmpty(authorization)) {
			authorization = "X-AUTH-TOKEN";
		}
		this.TOKEN_AUTHORIZATION  = authorization;
		
		String OutTimeStr = Property.getValue("jwt.token.outtime");
		long OutTime = 1800;
		if(StringUtils.isEmpty(OutTimeStr)) {
			OutTime  = Long.parseLong(OutTimeStr);
		}
		this.TOKEN_OUT_TIME=OutTime;
		
		String currenuserkey = Property.getValue("jwt.current.user");
		if(StringUtils.isEmpty(currenuserkey)) {
			currenuserkey = "CURRENT_TOKEN_USER_NAME";
		}
		this.CURRENT_USER_NAME  = currenuserkey;
	}

	public static JwtConstants get() {
		return jwtConstants;
	}
	public String getJWT_SECRET() {
		return JWT_SECRET;
	}
	public String getTOKEN_AUTHORIZATION() {
		return TOKEN_AUTHORIZATION;
	}
	public long getTOKEN_OUT_TIME() {
		return TOKEN_OUT_TIME;
	}
	public String getCURRENT_USER_NAME() {
		return CURRENT_USER_NAME;
	}
	
}
