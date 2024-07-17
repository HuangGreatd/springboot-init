package com.juzipi.controller;

import com.juzipi.common.BaseResponse;
import com.juzipi.common.ResultUtils;
import com.juzipi.domain.dto.req.UserRegisterRequest;
import com.juzipi.domain.entity.User;
import com.juzipi.domain.vo.UserVO;
import com.juzipi.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
	
	@GetMapping("/list")
	public BaseResponse<List<UserVO>> getUserList(){
		return ResultUtils.success(userService.getUserList());
	}
	
	@PostMapping("/register")
	public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest){
		
		return ResultUtils.success(userService.register(userRegisterRequest));
	}

}
