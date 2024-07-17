package com.juzipi.service.impl;

import java.util.Date;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzipi.common.ErrorCode;
import com.juzipi.domain.dto.req.UserRegisterRequest;
import com.juzipi.domain.entity.User;
import com.juzipi.domain.vo.UserVO;
import com.juzipi.exception.BusinessException;
import com.juzipi.service.UserService;
import com.juzipi.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 73782
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-07-15 20:47:38
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
		implements UserService {
	
	@Resource
	private UserMapper userMapper;
	
	private static String SALT = "juzipi";
	
	/**
	 * 获取用户列表(仅管理员可用) 管理员控权未实现todo
	 *
	 * @return
	 */
	@Override
	public List<UserVO> getUserList() {
		List<User> userList = userMapper.selectList(null);
		List<UserVO> userVOList = userList.stream().map(this::convertToUserVO).collect(Collectors.toList());
		return userVOList;
	}
	
	@Override
	public Long register(UserRegisterRequest userRegisterRequest) {
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String userName = userRegisterRequest.getUserName();
		String checkPassword = userRegisterRequest.getCheckPassword();
		
		//校验
		if (!checkPassword.equals(userPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
		}
		if (StrUtil.isEmpty(userAccount)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		if (StrUtil.isEmpty(userPassword) || userPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		if (StrUtil.isEmpty(userName)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		synchronized (userAccount.intern()) {
			//账号不能重复
			LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(User::getUserAccount, userAccount);
			User one = this.getOne(queryWrapper);
			if (one != null) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
			}
			
			//加密密码
			String encryptPassword = md5Password(userPassword);
			//插入数据库
			User user = new User();
			user.setUserAccount(userAccount);
			user.setUserPassword(encryptPassword);
			user.setUserName(userName);
			boolean save = this.save(user);
			if (!save) {
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
			}
			return user.getId();
		}
	}
	
	
	@Override
	public UserVO login(String userAccount, String userPassword) {
		if (StrUtil.isEmpty(userAccount) || StrUtil.isEmpty(userPassword) || userPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		return null;
	}
	
	
	private String md5Password(String password) {
		String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
		return encryptPassword;
	}
	
	//转换user -> userVO
	private UserVO convertToUserVO(User user) {
		UserVO userVO = new UserVO();
		userVO.setId(user.getId());
		userVO.setUserAccount(user.getUserAccount());
		userVO.setUserName(user.getUserName());
		userVO.setUserAvatar(user.getUserAvatar());
		userVO.setUserProfile(user.getUserProfile());
		userVO.setCreateTime(user.getCreateTime());
		return userVO;
	}
}




