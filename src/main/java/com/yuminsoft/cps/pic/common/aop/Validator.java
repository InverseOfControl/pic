package com.yuminsoft.cps.pic.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据效验
 * 
 * @author YM10138
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Validators.class) // Java 8 重复注解机制
@Target({ ElementType.METHOD })
public @interface Validator {
	// Key
	String key();

	// 失败信息
	String errormsg();

	// 数据类型
	Class<?> clazz() default String.class;

	// 验证类型
	ValiType type() default ValiType.NotNull;

	// ValiTypeIN
	String in() default "";
}
