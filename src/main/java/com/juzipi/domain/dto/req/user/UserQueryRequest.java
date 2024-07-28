package com.juzipi.domain.dto.req.user;

import com.juzipi.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author juzipi
 * @date 2024/7/19 8:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
	private Long id;
	
	/**
	 * 开放平台id
	 */
	private String unionId;
	
	/**
	 * 公众号openId
	 */
	private String mpOpenId;
	
	/**
	 * 用户昵称
	 */
	private String userName;
	
	/**
	 * 简介
	 */
	private String userProfile;
	
	/**
	 * 用户角色：user/admin/ban
	 */
	private String userRole;
	
	private static final long serialVersionUID = 1L;
}
