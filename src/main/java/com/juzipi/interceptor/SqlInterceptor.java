package com.juzipi.interceptor;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

/**
 * @author juzipi
 * @date 2024/7/20 15:17
 */
@Intercepts({
		@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
		@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
		@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Slf4j
public class SqlInterceptor implements Interceptor {
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		// 统计sql执行耗时
		long startTime = System.currentTimeMillis();
		Object proceed = invocation.proceed();
		long endTime = System.currentTimeMillis();
		String printSql = null;
		try {
			printSql = generateSql(invocation);
		} catch (Exception exception) {
			log.error("获取sql异常", exception);
		} finally {
			long costTime = endTime - startTime;
			log.info("\n 执行SQL耗时：{}ms \n 执行SQL：{}", costTime, printSql);
		}
		return proceed;
	}
	private static String generateSql(Invocation invocation) {
		MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
		Object parameter = null;
		if (invocation.getArgs().length > 1) {
			parameter = invocation.getArgs()[1];
		}
		Configuration configuration = statement.getConfiguration();
		BoundSql boundSql = statement.getBoundSql(parameter);
		// 获取参数对象
		Object parameterObject = boundSql.getParameterObject();
		// 获取参数映射
		List<ParameterMapping> params = boundSql.getParameterMappings();
		// 获取到执行的SQL
		String sql = boundSql.getSql();
		// SQL中多个空格使用一个空格代替
		sql = sql.replaceAll("[\\s]+", " ");
		if (!ObjectUtils.isEmpty(params) && !ObjectUtils.isEmpty(parameterObject)) {
			// TypeHandlerRegistry 是 MyBatis 用来管理 TypeHandler 的注册器 TypeHandler 用于在 Java 类型和 JDBC 类型之间进行转换
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			// 如果参数对象的类型有对应的 TypeHandler，则使用 TypeHandler 进行处理
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
			} else {
				// 否则，逐个处理参数映射
				for (ParameterMapping param : params) {
					// 获取参数的属性名
					String propertyName = param.getProperty();
					MetaObject metaObject = configuration.newMetaObject(parameterObject);
					// 检查对象中是否存在该属性的 getter 方法，如果存在就取出来进行替换
					if (metaObject.hasGetter(propertyName)) {
						Object obj = metaObject.getValue(propertyName);
						sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
						// 检查 BoundSql 对象中是否存在附加参数
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						Object obj = boundSql.getAdditionalParameter(propertyName);
						sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
					} else {
						// SQL匹配不上，带上“缺失”方便找问题
						sql = sql.replaceFirst("\\?", "缺失");
					}
				}
			}
		}
		return sql;
	}
	private static String getParameterValue(Object object) {
		String value = "";
		if (object instanceof String) {
			value = "'" + object + "'";
		} else if (object instanceof Date) {
			DateFormat format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
			value = "'" + format.format((Date) object) + "'";
		} else if (!ObjectUtils.isEmpty(object)) {
			value = object.toString();
		}
		return value;
	}
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}
}


