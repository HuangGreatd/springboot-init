package com.juzipi.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.juzipi.domain.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author juzipi
 * @date 2024/7/15 22:29
 */
@Data
public class UserVO implements Serializable {
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 账号
	 */
	private String userAccount;
	
	/**
	 * 用户昵称
	 */
	private String userName;
	
	/**
	 * 用户头像
	 */
	private String userAvatar;
	
	/**
	 * 用户简介
	 */
	private String userProfile;
	
	private String userRole;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
	
	
}
