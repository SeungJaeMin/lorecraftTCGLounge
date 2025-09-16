package com.lorecraft.tcglounge.security;

import com.lorecraft.tcglounge.entity.User;
import com.lorecraft.tcglounge.service.UserService;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 현재 인증된 사용자 정보를 컨트롤러 메서드 파라미터에 자동으로 주입하는 ArgumentResolver
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    
    private final UserService userService;
    
    public CurrentUserArgumentResolver(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) && 
               (User.class.isAssignableFrom(parameter.getParameterType()) ||
                parameter.getParameterType().isAssignableFrom(User.class));
    }
    
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) throws Exception {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            CurrentUser annotation = parameter.getParameterAnnotation(CurrentUser.class);
            if (annotation != null && annotation.required()) {
                throw new IllegalStateException("User authentication required but not found");
            }
            return null;
        }
        
        // JWT 필터에서 설정한 principal은 userId입니다
        Long userId = (Long) authentication.getPrincipal();
        
        return userService.findById(userId)
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));
    }
}