package com.juzipi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzipi.annotation.AuthCheck;
import com.juzipi.common.BaseResponse;
import com.juzipi.common.DeleteRequest;
import com.juzipi.common.ErrorCode;
import com.juzipi.common.ResultUtils;
import com.juzipi.constant.UserConstant;
import com.juzipi.domain.dto.req.user.*;
import com.juzipi.domain.entity.User;
import com.juzipi.domain.vo.UserVO;
import com.juzipi.exception.BusinessException;
import com.juzipi.exception.ThrowUtils;
import com.juzipi.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.juzipi.constant.UserConstant.ADMIN_ROLE_NUM;
import static com.sun.javafx.font.FontResource.SALT;

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
	 * 查询用户列表
	 *
	 * @return
	 */
	@GetMapping("/list")
	public BaseResponse<List<UserVO>> getUserList() {
		return ResultUtils.success(userService.getUserList());
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
		UserVO loginUser = userService.getLoginUserVo(request);
		return ResultUtils.success(loginUser);
	}
	
	// endregion
	
	// region 增删改查
	@PostMapping("/add")
	@AuthCheck(mustRole = ADMIN_ROLE_NUM)
	public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request){
		if (userAddRequest == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
		}
		User user = new User();
		BeanUtils.copyProperties(userAddRequest,user);
		// 默认密码 12345678
		String defaultPassword = "12345678";
		String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
		user.setUserPassword(encryptPassword);
		boolean result = userService.save(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(user.getId());
	}
	
	/**
	 * 删除用户
	 *
	 * @param deleteRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = ADMIN_ROLE_NUM)
	public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean b = userService.removeById(deleteRequest.getId());
		return ResultUtils.success(b);
	}
	
	/**
	 * 更新用户
	 *
	 * @param userUpdateRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = ADMIN_ROLE_NUM)
	public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
											HttpServletRequest request) {
		if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = new User();
		BeanUtils.copyProperties(userUpdateRequest, user);
		boolean result = userService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取用户（仅管理员）
	 *
	 * @param id
	 * @param request
	 * @return
	 */
	@GetMapping("/get")
	@AuthCheck(mustRole = ADMIN_ROLE_NUM)
	public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
		if (id <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getById(id);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
		return ResultUtils.success(user);
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
	 * 分页获取用户列表（仅管理员）
	 *
	 * @param userQueryRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = ADMIN_ROLE_NUM)
	public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
												   HttpServletRequest request) {
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		Page<User> userPage = userService.page(new Page<>(current, size),
				userService.getQueryWrapper(userQueryRequest));
		return ResultUtils.success(userPage);
	}
	
	
	@GetMapping("/list/user")
	@AuthCheck(mustRole = ADMIN_ROLE_NUM)
	public BaseResponse<Page<User>> getUserListByAdmin(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request){
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		Page<User> userPage = userService.page(new Page<>(current, size),
				userService.getQueryWrapper(userQueryRequest));
		return ResultUtils.success(userPage);
	}
	
	/**
	 * 分页获取用户封装列表
	 *
	 * @param userQueryRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
													   HttpServletRequest request) {
		if (userQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		Page<User> userPage = userService.page(new Page<>(current, size),
				userService.getQueryWrapper(userQueryRequest));
		Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
		List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
		userVOPage.setRecords(userVO);
		return ResultUtils.success(userVOPage);
	}
	
	
	//endregion
	/**
	 * 更新个人信息
	 *
	 * @param userUpdateMyRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/update/my")
	public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
											  HttpServletRequest request) {
		if (userUpdateMyRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		User user = new User();
		BeanUtils.copyProperties(userUpdateMyRequest, user);
		user.setId(loginUser.getId());
		boolean result = userService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
}
