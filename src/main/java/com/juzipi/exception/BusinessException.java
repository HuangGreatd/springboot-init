package com.juzipi.exception;

import com.juzipi.common.ErrorCode;

/**
 * 自定义异常处理
 *
 * @author juzipi
 * @date 2024/7/17 8:34
 */
public class BusinessException extends RuntimeException {
	/**
	 * 错误码
	 */
	private final int code;
	
	public BusinessException(int code, String message) {
		super(message);
		this.code = code;
	}
	
	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.code = errorCode.getCode();
	}
	
	public BusinessException(ErrorCode errorCode, String message) {
		super(message);
		this.code = errorCode.getCode();
	}
	
	public int getCode() {
		return code;
	}
}
