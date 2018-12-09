package hao.framework.database.page.dialect.imp;

import hao.framework.database.page.dialect.Dialect;

/**
 * 
 * @描述:TODO
 * @作者:jh
 * @时间:2016年5月27日 下午9:36:13
 */
public class OracleDialect implements Dialect {

    @Override
    public String getLimitString(String sql, int offset, int limit) {

        sql = sql.trim();
        boolean isForUpdate = false;
        if (sql.toLowerCase().endsWith(" for update")) {
            sql = sql.substring(0, sql.length() - 11);
            isForUpdate = true;
        }

        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);

        pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");

        pagingSelect.append(sql);

        pagingSelect.append(" ) row_ ) where rownum_ > " + offset + " and rownum_ <= " + (offset + limit));

        if (isForUpdate) {
            pagingSelect.append(" for update");
        }

        return pagingSelect.toString();
    }

    @Override
    public String getCountString(String sql) {
        // TODO Oracle分页查询
    	String countSql = "";
    	countSql = "select count(1) from ("+sql+") temp_count_table ";
    	return countSql;
    }
}
