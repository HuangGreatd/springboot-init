package com.juzipi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author juzipi
 * @date 2024/7/17 21:01
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
	
	/**
	 * 必须拥有某个角色
	 *
	 * @return
	 */
	int mustRole() default 2;
}
