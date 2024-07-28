package com.juzipi.aop;

import com.juzipi.annotation.AuthCheck;
import com.juzipi.common.ErrorCode;
import com.juzipi.domain.enums.UserRoleEnums;
import com.juzipi.domain.vo.UserVO;
import com.juzipi.exception.BusinessException;
import com.juzipi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author juzipi
 * @date 2024/7/17 21:04
 */
@Aspect
@Component
@Slf4j
public class AuthInterceptor {
	
	@Resource
	private UserService userService;
	
	@Around("@annotation(authCheck)")
	public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
		int mustRoleNum = authCheck.mustRole();
		String mustRole = UserRoleEnums.getRoleName(mustRoleNum);
		
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		//当前登录用户
		UserVO loginUser = userService.getLoginUserVo(request);
		String userRole = loginUser.getUserRole();
		if (userRole == null) {
			return joinPoint.proceed();
		}
		if (UserRoleEnums.BAN.equals(userRole)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		//必须有管理员权限
		if (UserRoleEnums.ADMIN.getRole().equals(mustRole)) {
			//用户没有管理员权限，拒绝
			if (!UserRoleEnums.ADMIN.getRole().equals(userRole)) {
				throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
			}
		}
		return joinPoint.proceed();
		
	}
}
