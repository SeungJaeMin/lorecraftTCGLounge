package com.lorecraft.tcglounge.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드 파라미터에 현재 인증된 사용자 정보를 주입하기 위한 어노테이션
 * ArgumentResolver와 함께 사용되어 자동으로 현재 사용자 정보를 바인딩합니다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
    boolean required() default true;
}