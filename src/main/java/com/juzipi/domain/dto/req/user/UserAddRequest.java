package com.juzipi.domain.dto.req.user;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * @author juzipi
 * @date 2024/7/28 16:25
 */
@Data
public class UserAddRequest implements Serializable {
	/**
	 * 用户昵称
	 */
	private String userName;
	
	/**
	 * 账号
	 */
	private String userAccount;
	
	/**
	 * 用户头像
	 */
	private String userAvatar;
	
	/**
	 * 用户角色: user  1 , admin  2
	 */
	private Integer userRole;
	
	private static final long serialVersionUID = 1L;
}
