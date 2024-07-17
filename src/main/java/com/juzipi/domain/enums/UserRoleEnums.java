package com.juzipi.domain.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author juzipi
 * @date 2024/7/16 8:24
 */
public enum UserRoleEnums {
	USER(1, "user"),
	ADMIN(2, "admin"),
	BAN(3, "ban");
	
	private int code;
	private String role;
	
	UserRoleEnums(int code, String role) {
		this.code = code;
		this.role = role;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public static UserRoleEnums getRole(int code) {
		if (code == 0) {
			return null;
			
		}
		for (UserRoleEnums role : UserRoleEnums.values()) {
			if (role.getCode() == code) {
				return role;
			}
		}
		return null;
	}
	
	/**
	 * 获取值列表
	 *
	 * @return
	 */
	public static List<String> getValues() {
		return Arrays.stream(values()).map(item -> item.role).collect(Collectors.toList());
	}
}
