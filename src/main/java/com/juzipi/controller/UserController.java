package com.juzipi.controller;

import cn.hutool.http.server.HttpServerRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzipi.common.BaseResponse;
import com.juzipi.common.ErrorCode;
import com.juzipi.common.ResultUtils;
import com.juzipi.domain.dto.req.UserLoginRequest;
import com.juzipi.domain.dto.req.UserQueryParamRequest;
import com.juzipi.domain.dto.req.UserQueryRequest;
import com.juzipi.domain.dto.req.UserRegisterRequest;
import com.juzipi.domain.entity.User;
import com.juzipi.domain.vo.UserVO;
import com.juzipi.exception.BusinessException;
import com.juzipi.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author juzipi
 * @date 2024/7/15 20:50
 */
@RestController
@RequestMapping("/user")
public class UserController {
	@Resource
	private UserService userService;
	
	/**
	 * 查询用户列表
	 *
	 * @return
	 */
	@GetMapping("/list")
	public BaseResponse<List<UserVO>> getUserList() {
		return ResultUtils.success(userService.getUserList());
	}
	
	/**
	 * 用户注册
	 *
	 * @param userRegisterRequest
	 * @return
	 */
	@PostMapping("/register")
	public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
		return ResultUtils.success(userService.register(userRegisterRequest));
	}
	
	/**
	 * 用户登录
	 *
	 * @param userLoginRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/login")
	public BaseResponse<UserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
		return ResultUtils.success(userService.login(userLoginRequest, request));
	}
	
	/**
	 * 根据条件查询用户
	 *
	 * @param userQueryParamRequest
	 * @return
	 */
	@PostMapping("/queryByParam")
	public BaseResponse<List<UserVO>> queryByParam(@RequestBody UserQueryParamRequest userQueryParamRequest) {
		return ResultUtils.success(userService.queryOneByParam(userQueryParamRequest));
	}
	
	/**
	 * 用户退出
	 *
	 * @param request
	 * @return
	 */
	@PostMapping("/logout")
	public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
		if (request == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean result = userService.userLogout(request);
		return ResultUtils.success(result);
	}
	
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/get/login")
	public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
		UserVO loginUser = userService.getLoginUser(request);
		return ResultUtils.success(loginUser);
	}
	
	@GetMapping("/list/user")
	public BaseResponse<Page<User>> getUserListByAdmin(@RequestBody UserQueryRequest userQueryRequest,HttpServletRequest request){
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		Page<User> userPage = userService.page(new Page<>(current, size),
				userService.getQueryWrapper(userQueryRequest));
		return ResultUtils.success(userPage);
	}
	
	@GetMapping("/test")
	public BaseResponse<String> getUserListTest() {
		userService.selectListUser();
		return ResultUtils.success("ok");
	}
	
}
