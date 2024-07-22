package com.juzipi.aop;

import cn.hutool.core.date.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author juzipi
 * @date 2024/7/18 20:30
 */
@Aspect
@Component
@Slf4j
public class LogInterceptor {
	
	public Object doInterceptor(ProceedingJoinPoint point) throws Throwable{
		// 计时
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		//获取请求路径
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
		//生成请求唯一id
		String requestId = UUID.randomUUID().toString();
		String url = httpServletRequest.getRequestURI();
		//获取请求参数
		Object[] args = point.getArgs();
		String reqParam = "[" + StringUtils.join(args, ",") + "]";
		// 输出请求日志
		log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
				httpServletRequest.getRemoteHost(), reqParam);
		Object result = point.proceed();
		//输出响应日志
		stopWatch.stop();
		long totalTimeMillis = stopWatch.getTotalTimeMillis();
		log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
		return result;
		
	}
}
