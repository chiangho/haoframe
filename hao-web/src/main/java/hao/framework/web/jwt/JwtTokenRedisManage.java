package hao.framework.web.jwt;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 通过Redis存储和验证token的实现类
 * @author ScienJus
 * @date 2015/7/31.
 */
public class JwtTokenRedisManage implements JwtTokenManageInterface {
	
	@Autowired
    private StringRedisTemplate redisTemplate;
	
	private final String redis_user_prefix="user_info_";

	private String getUserTokenKey(String userId) {
		return redis_user_prefix+userId;
	}
	
    /**
     * 生成TOKEN
     */
    public String createToken(String userId,final Map<String,Object> claims) {
        //使用uuid作为源token
        JwtBuilder jwtBuilder = Jwts.builder()
        		.setId(UUID.randomUUID().toString())
        		.setSubject(userId)
        		.setIssuedAt(new Date());
        if(claims!=null&&claims.size()>0) {
        	for(String key:claims.keySet()) {
        		jwtBuilder.claim(key, claims);
        	}
        }
        jwtBuilder.signWith(SignatureAlgorithm.HS256, JwtConstants.get().getJWT_SECRET());
        String token = jwtBuilder.compact();
        //存储到redis并设置过期时间
        //同一账号不同设备登录会互斥。不允许一个账号在两个地方登录
        redisTemplate
        .boundValueOps(getUserTokenKey(userId))
        .set(token, JwtConstants.get().getTOKEN_OUT_TIME(), TimeUnit.SECONDS);
        return token;
    }
    
    public JwtToken getToken(String userid,String token) {
        return new JwtToken(userid, token);
    }

    public boolean checkToken(JwtToken model) {
        if (model == null) {
            return false;
        }
        String token = (String) redisTemplate.boundValueOps(getUserTokenKey(model.getUserId())).get();
        if (token == null || !token.equals(model.getToken())) {
            return false;
        }
        //如果验证成功，说明此用户进行了一次有效操作，延长token的过期时间
        redisTemplate.boundValueOps(getUserTokenKey(model.getUserId())).expire(JwtConstants.get().getTOKEN_OUT_TIME(), TimeUnit.SECONDS);
        return true;
    }

    public void deleteToken(String userId) {
        redisTemplate.delete(getUserTokenKey(userId));
    }
}
