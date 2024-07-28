package com.juzipi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzipi.common.ErrorCode;
import com.juzipi.constant.CommonConstant;
import com.juzipi.domain.dto.req.user.UserLoginRequest;
import com.juzipi.domain.dto.req.user.UserQueryParamRequest;
import com.juzipi.domain.dto.req.user.UserQueryRequest;
import com.juzipi.domain.dto.req.user.UserRegisterRequest;
import com.juzipi.domain.entity.User;
import com.juzipi.domain.enums.UserRoleEnums;
import com.juzipi.domain.vo.UserVO;
import com.juzipi.exception.BusinessException;
import com.juzipi.service.UserService;
import com.juzipi.mapper.UserMapper;
import com.juzipi.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.juzipi.constant.UserConstant.USER_LOGIN_STATE;

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
		if (StrUtil.isEmpty(userAccount) || StrUtil.isEmpty(userPassword) || StrUtil.isEmpty(checkPassword) || StrUtil.isEmpty(userName)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入必填信息");
		}
		
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
	public UserVO login(UserLoginRequest userLoginRequest, HttpServletRequest request) {
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		if (StrUtil.isEmpty(userAccount) || StrUtil.isEmpty(userPassword) || userPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		//加密密码
		String entryptPassowrd = md5Password(userPassword);
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(User::getUserAccount, userAccount);
		queryWrapper.eq(User::getUserPassword, entryptPassowrd);
		User user = this.getOne(queryWrapper);
		if (user == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		}
		
		if (!entryptPassowrd.equals(user.getUserPassword())) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误~");
		}
		
		request.getSession().setAttribute(USER_LOGIN_STATE, user);
		return convertToUserVO(user);
	}
	
	@Override
	public List<UserVO> queryOneByParam(UserQueryParamRequest userQueryParamRequest) {
		String userAccount = userQueryParamRequest.getUserAccount();
		String userName = userQueryParamRequest.getUserName();
		Long userId = userQueryParamRequest.getUserId();
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
		if (!StrUtil.isEmpty(userAccount)) {
			queryWrapper.like(User::getUserAccount, userAccount);
		}
		if (!StrUtil.isEmpty(userName)) {
			queryWrapper.like(User::getUserName, userName);
		}
		if (!ObjUtil.isEmpty(userId) && userId > 0) {
			queryWrapper.eq(User::getId, userId);
		}
		List<User> userList = this.list(queryWrapper);
		List<UserVO> userVOList = userList.stream().map(this::convertToUserVO).collect(Collectors.toList());
		return userVOList;
	}
	
	/**
	 * 获取当前登录用户 （脱敏）
	 *
	 * @param request
	 * @return
	 */
	@Override
	public UserVO getLoginUserVo(HttpServletRequest request) {
		Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
		if (userObj == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		//todo 这里再查询的理由是什么？
		User currentUser = (User) userObj;
		Long userId = currentUser.getId();
		currentUser = this.getById(userId);
		if (currentUser == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		return convertToUserVO(currentUser);
	}
	
	/**
	 * 获取当前登录用户(全部)
	 *
	 * @param request
	 * @return
	 */
	@Override
	public User getLoginUser(HttpServletRequest request) {
		Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
		if (userObj == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		User currentUser = (User) userObj;
		Long userId = currentUser.getId();
		currentUser = this.getById(userId);
		if (currentUser == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		return currentUser;
	}
	
	
	@Override
	public List<UserVO> getUserVO(List<User> userList) {
		if (CollUtil.isEmpty(userList)) {
			return new ArrayList<>();
		}
		return userList.stream().map(this::convertToUserVO).collect(Collectors.toList());
	}
	
	@Override
	public boolean userLogout(HttpServletRequest request) {
		if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
		}
		// 移除登录态
		request.getSession().removeAttribute(USER_LOGIN_STATE);
		return true;
	}
	
	@Override
	public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
		if (userQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
		}
		Long id = userQueryRequest.getId();
		String unionId = userQueryRequest.getUnionId();
		String mpOpenId = userQueryRequest.getMpOpenId();
		String userName = userQueryRequest.getUserName();
		String userProfile = userQueryRequest.getUserProfile();
		String userRole = userQueryRequest.getUserRole();
		String sortField = userQueryRequest.getSortField();
		String sortOrder = userQueryRequest.getSortOrder();
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq(id != null, "id", id);
		queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
		queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
		queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
		queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
		queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
		queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	@Override
	public void selectListUser() {
		String userName = "juzipi";
		List<User> userList = userMapper.selectListUser(userName);
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
		Integer userRole = user.getUserRole();
		String userRoleStr = UserRoleEnums.getRole(userRole).getRole();
		userVO.setUserRole(userRoleStr);
		return userVO;
	}
	
	
}




