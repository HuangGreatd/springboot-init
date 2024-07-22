package com.juzipi.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.juzipi.domain.dto.req.UserLoginRequest;
import com.juzipi.domain.dto.req.UserQueryParamRequest;
import com.juzipi.domain.dto.req.UserQueryRequest;
import com.juzipi.domain.dto.req.UserRegisterRequest;
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
	
	UserVO getLoginUser(HttpServletRequest request);
	
	boolean userLogout(HttpServletRequest request);
	
	QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
	
	void selectListUser();
}
