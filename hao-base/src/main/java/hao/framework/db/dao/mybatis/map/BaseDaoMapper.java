package hao.framework.db.dao.mybatis.map;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import hao.framework.db.dao.mybatis.SqlBuilder;
import hao.framework.db.sql.SqlSearch;
import hao.framework.db.sql.SqlWhere;


/***
 * dao层基础类
 * @author chianghao
 * @param <T>
 */
public interface BaseDaoMapper {
	
	
	/***
	 * 增加
	 * @param bean
	 * @return
	 */
	@InsertProvider(type=SqlBuilder.class,method="buildInsertSql")
	public <T> int insert(T bean);
	
	
	/***
	 * 修改
	 * @param clazz
	 * @param fields
	 * @param id
	 * @return
	 */
	@UpdateProvider(type=SqlBuilder.class,method="buildUpdateByIdSql")
	public int updateById(
			@Param("clazz") Class<?> clazz,
			@Param("fields") Map<String,Object> fields,
			@Param("id") String id
			);
	
	
	
	/**
	 * 更新
	 * @param fields
	 * @param sqlWhere
	 * @return
	 */
	@UpdateProvider(type=SqlBuilder.class,method="buildUpdateSql")
	public <T> int update(
			@Param("clazz") Class<?> clazz,
			@Param("fields") Map<String,Object> fields,
			@Param("sqlWhere") SqlWhere sqlWhere
			);
	
	/***
	 * 删除
	 * @param clazz
	 * @param id
	 * @return
	 */
	@DeleteProvider(type=SqlBuilder.class,method="buildDeleteByIdSql")
	public int deleteById(@Param("clazz") Class<?> clazz,@Param("id") String id);
	
	
	/**
	 * 删除
	 * @param clazz
	 * @param sqlWhere
	 * @return
	 */
	@DeleteProvider(type=SqlBuilder.class,method="buildDeleteSql")
	public int delete(@Param("clazz") Class<?> clazz,@Param("sqlWhere") SqlWhere sqlWhere);
	
	/***
	 * 根据条件查询对象
	 * @param <T>
	 * @param <T>
	 * @param clazz
	 * @param fields
	 * @return
	 */
	@SelectProvider(type=SqlBuilder.class,method="buildQuerySql")
	public  Map<String,Object> queryBean(@Param("sqlSearch") SqlSearch sqlSearch);
	
	
	/***
	 * 查询列表信息
	 * @param clazz
	 * @param where
	 * @return
	 */
	@SelectProvider(type=SqlBuilder.class,method="buildQuerySql")
	public  List<Map<String,Object>> queryList(@Param("sqlSearch") SqlSearch sqlSearch);
}
