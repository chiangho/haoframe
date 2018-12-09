package hao.framework.web.shiro.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisSessionDao extends AbstractSessionDAO {

	private Logger logger = LoggerFactory.getLogger("--------------"+this.getClass()+"session会话日志：--------------");
	
	private static final String KEY_PREFIX = "shiro_web_service_session:"; 
	
	//private RedisManager redisManager;
	
	private long         sessionTimeout;
	
	@Autowired  
	RedisTemplate<String,Session> redisTemplate;  
	
	@Override
	public void update(Session session) throws UnknownSessionException {
		 this.saveSession(session);
	}

	@Override
	public void delete(Session session) {
		if (session == null || session.getId() == null) {
            logger.error("session or session id is null");
            return;
        }
//        try {
//			redisManager.delete(KEY_PREFIX + session.getId());
//		} catch (HaoException e) {
//			e.printStackTrace();
//		}
		redisTemplate.delete(KEY_PREFIX + session.getId());
//        redisTemplate.opsForValue().getOperations().delete(KEY_PREFIX + session.getId());
//        redisTemplate.opsForHash().delete("");
	}

	@Override
	public Collection<Session> getActiveSessions() {
		Set<Session> sessions = new HashSet<Session>();  
		try {
			Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
			for(String key:keys) {
				Session s = redisTemplate.opsForValue().get(key);
				//Session s = SerializerUtil.deserialize(redisManager.get(SerializerUtil.deserialize(key)));  
                sessions.add(s); 
			}
//			Set<byte[]> keys = redisManager.keys(KEY_PREFIX + "*");  
//	        if(keys != null && keys.size()>0){  
//	            for(byte[] key : keys){  
//	                Session s = SerializerUtil.deserialize(redisManager.get(SerializerUtil.deserialize(key)));  
//	                sessions.add(s);  
//	            } 
//	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
        return sessions;  
	}

//	 public Set<byte[]> keys(String key){
//        if (StringUtils.isEmpty(key)) {
//            return null;
//        }
//        Set<byte[]> bytesSet=new HashSet<byte[]>();
//        
//        try {
//			bytesSet = redisManager.keys(key);
//		} catch (HaoException e) {
//			e.printStackTrace();
//		}
//        return bytesSet;
//    }
	
	
	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		if(sessionId == null){  
            logger.error("session id is null");  
            return null;  
        }  
        Session s=(Session)redisTemplate.opsForValue().get(KEY_PREFIX + sessionId);
//        try {
//			s = redisManager.get(KEY_PREFIX + sessionId);
//		} catch (HaoException e) {
//			e.printStackTrace();
//		}  
        return s;  
	}

	private void saveSession(Session session) throws UnknownSessionException{
        if (session == null || session.getId() == null) {
            logger.error("session or session id is null");  
            return;
        }
        if(sessionTimeout==0) {
        	//如果为零，默认30分钟
        	sessionTimeout=1800000;
        }
        session.setTimeout(sessionTimeout);
        int redisOuttime = (int) (sessionTimeout/1000);
        redisTemplate.opsForValue().set(KEY_PREFIX + session.getId(), session, redisOuttime, TimeUnit.SECONDS);
//        try {
//			redisManager.put(KEY_PREFIX + session.getId(),session,redisOuttime);
//		} catch (HaoException e) {
//			e.printStackTrace();
//		}
    }

//	public RedisManager getRedisManager() {
//		return redisManager;
//	}
//
//	public void setRedisManager(RedisManager redisManager) {
//		this.redisManager = redisManager;
//	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	
}
