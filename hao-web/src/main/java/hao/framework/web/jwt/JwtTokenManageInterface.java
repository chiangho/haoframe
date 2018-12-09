package hao.framework.web.jwt;

import java.util.Map;

/**
 * JWT Token Manage Interface
 * @author chianghao
 *
 */
public interface JwtTokenManageInterface {

	/**
     * 创建一个token关联上指定用户
     * @param username  用户账号
     * @param userpaswd 用户密码
     * @return 生成的token
     */
    public String createToken(String userId,final Map<String,Object> claims);

    /**
     * 检查token是否有效
     * @param model token
     * @return 是否有效
     */
    public boolean checkToken(JwtToken model);

    /**
     * 从字符串中解析token
     * @param authentication 加密后的字符串
     * @return
     */
    public JwtToken getToken(String userid,String token);

    /**
     * 清除token
     * @param username 登录用户账号
     */
    public void deleteToken(String username);
	
	
}
