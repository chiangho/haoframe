package hao.framework.db.page;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hao.framework.db.page.dialect.Dialect;
import hao.framework.db.page.dialect.MySqlDialect;
import hao.framework.utils.ClassUtils;

/***
 * 分页连接器
 * 
 * @author chianghao
 */
@Intercepts({
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }), })
public class PaginationInterceptor implements Interceptor {

	Logger log = LogManager.getLogger(this.getClass());//Logger.getLogger(this.getClass());

	protected Dialect dialect;// 对应的插件

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Executor exe = (Executor) invocation.getTarget();
		Object[] args = invocation.getArgs();
		Object parameter = args[1];
		MappedStatement ms = (MappedStatement) args[0];
		BoundSql boundSql;
		Connection connection = exe.getTransaction().getConnection();
		if (log.isDebugEnabled()) {
			log.info("数据源驱动为：" + connection.getMetaData().getDriverName().toUpperCase());
		}
		if (connection.getMetaData().getDriverName().toUpperCase().indexOf("MYSQL") != -1) {
			dialect = new MySqlDialect();
		}
		if (dialect != null) {
			String id = ms.getId();
			Page page = PageHelper.getPage(id);
			if (page != null) {
				if(log.isDebugEnabled()) {
					log.info("--------------执行分页查询-------------");
				}
				if (args.length == 4) {
					// 4 个参数时
					boundSql = ms.getBoundSql(parameter);
//					cacheKey = exe.createCacheKey(ms, parameter, rowBounds, boundSql);
				} else {
					// 6 个参数时
//					cacheKey = (CacheKey) args[4];
					boundSql = (BoundSql) args[5];
				}
				String originalSql = boundSql.getSql().trim();
				if (originalSql.endsWith(";")) {
					originalSql = originalSql.substring(0, originalSql.lastIndexOf(";"));
				}
				String countSql = dialect.getCountString(originalSql);
				page.setRowNum(SQLHelper.getCount(countSql, connection, ms, boundSql.getParameterObject(), boundSql, log));
				String pageSql = dialect.getLimitString(originalSql, page.getOffset(), page.getLimit());
				if(log.isDebugEnabled()) {
					log.info("==>被执行的sql语句为"+pageSql);
				}
				invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
				BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(),
						boundSql.getParameterObject());
				if (ClassUtils.getFieldValue(boundSql, "metaParameters") != null) {
					MetaObject mo = (MetaObject) ClassUtils.getFieldValue(boundSql, "metaParameters");
					ClassUtils.setFieldValue(newBoundSql, "metaParameters", mo);
				}
				MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
				invocation.getArgs()[0] = newMs;
				
				PageHelper.removePage(id);
			}
		} 
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}

	private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
				ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null) {
			for (String keyProperty : ms.getKeyProperties()) {
				builder.keyProperty(keyProperty);
			}
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.cache(ms.getCache());
		return builder.build();
	}

	public class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

}
