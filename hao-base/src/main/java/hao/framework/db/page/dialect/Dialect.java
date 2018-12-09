package hao.framework.db.page.dialect;

/**
 * 数据库方言抽象类
 * 
 * @author 蒋昊
 **/
public interface Dialect {
    /**
     * 得到分页sql
     * 
     * @param sql
     * @param offset
     * @param limit
     * @return
     */
    public String getLimitString(String sql, int offset, int limit);

    /**
     * 得到总数量 sql
     * 
     * @param sql
     * @return
     */
    public String getCountString(String sql);

}
