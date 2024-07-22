package com.juzipi.interceptor;

import com.juzipi.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author juzipi
 * @date 2024/7/20 16:10
 */
@Component
@Slf4j
@Intercepts({
		@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
		@Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
		@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
	
})
public class ResultSetInterceptor implements Interceptor {
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (invocation.getTarget() instanceof ResultSetHandler) {
			// 拦截 ResultSetHandler 的 handleResultSets 方法
			ResultSetHandler resultSetHandler = (ResultSetHandler) invocation.getTarget();
			Object result = invocation.proceed();
			
			System.out.println("result==>    " + result);
			
			if (result instanceof  List){
				List<?> resultList = (List<?>) result;
				List<?> collect = resultList.stream().filter(item -> {
					if (item instanceof User) {
						User user = (User) item;
						return "小张".equals(user.getUserAccount());
					}
					return false;
				}).collect(Collectors.toList());
				System.out.println("Filtered result==> " + collect);
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
		// 可以在这里设置一些属性
	}
}
