package com.juzipi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.juzipi.domain.dto.req.user.UserLoginRequest;
import com.juzipi.domain.dto.req.user.UserQueryParamRequest;
import com.juzipi.domain.dto.req.user.UserQueryRequest;
import com.juzipi.domain.dto.req.user.UserRegisterRequest;
import com.juzipi.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juzipi.domain.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 73782
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-07-15 20:47:38
*/
public interface UserService extends IService<User> {
	
	List<UserVO> getUserList();
	
	Long register(UserRegisterRequest userRegisterRequest);
	
	UserVO login(UserLoginRequest userLoginRequest, HttpServletRequest request );
	
	List<UserVO> queryOneByParam(UserQueryParamRequest userQueryParamRequest);
	
	UserVO getLoginUserVo(HttpServletRequest request);
	
	User getLoginUser(HttpServletRequest request);
	
	List<UserVO> getUserVO(List<User> userList);
	
	boolean userLogout(HttpServletRequest request);
	
	QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
	
	void selectListUser();
}
