package com.soongsil.swcontest.security;

import com.soongsil.swcontest.enums.RoleType;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class UserInfoArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Authenticated.class) != null &&
                parameter.getParameterType() == AuthInfo.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        UserDetails authentication = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new AuthInfo(
                webRequest.getHeader("Authorization"),
                authentication.getUsername(),
                mapRolesFromAuthorities(authentication.getAuthorities())
        );
    }

    private List<RoleType> mapRolesFromAuthorities(Collection<? extends GrantedAuthority> authorities) {
        List<RoleType> roles = new ArrayList<>();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        while (iter.hasNext()) {
            roles.add(RoleType.valueOf(iter.next().getAuthority()));
        }
        return roles;
    }
}
