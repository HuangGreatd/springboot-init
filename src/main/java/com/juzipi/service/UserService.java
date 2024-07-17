package com.juzipi.service;

import com.juzipi.domain.dto.req.UserRegisterRequest;
import com.juzipi.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juzipi.domain.vo.UserVO;

import java.util.List;

/**
* @author 73782
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-07-15 20:47:38
*/
public interface UserService extends IService<User> {
	
	List<UserVO> getUserList();
	
	Long register(UserRegisterRequest userRegisterRequest);
	
	UserVO login(String userAccount, String password);
}
