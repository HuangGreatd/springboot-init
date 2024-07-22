package com.juzipi.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author juzipi
 * @date 2024/7/20 16:10
 */
@Component
@Slf4j
@Intercepts({
		@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
		@Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
})
public class ExecutorInterceptor implements Interceptor {
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		// 获取原始SQL
		String originSql = statementHandler.getBoundSql().getSql();
		
		// 检查原始SQL中是否已经包含我们要添加的条件
		if (!originSql.contains("userAccount = '小张'")) {
			String modifiedSql;
			if (originSql.toUpperCase().contains("WHERE")) {
				// 如果包含WHERE条件，添加AND
				modifiedSql = originSql + " AND userAccount = '小张'";
			} else {
				// 如果不包含WHERE条件，直接添加WHERE
				modifiedSql = originSql + " WHERE userAccount = '小张'";
			}
			
			System.out.println("modifiedSql===>    " + modifiedSql);
			
			Field field = statementHandler.getBoundSql().getClass().getDeclaredField("sql");
			field.setAccessible(true);
			field.set(statementHandler.getBoundSql(), modifiedSql);
			System.out.println(field.get(statementHandler.getBoundSql()));
		}
		
		return invocation.proceed();
	}
	
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}
	
	@Override
	public void setProperties(Properties properties) {
		// 可以在这里设置一些属性
	}
}
