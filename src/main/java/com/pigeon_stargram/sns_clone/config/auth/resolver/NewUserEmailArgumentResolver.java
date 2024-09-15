package com.pigeon_stargram.sns_clone.config.auth.resolver;

import com.pigeon_stargram.sns_clone.config.auth.annotation.NewUserEmail;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @NewUserEmail 어노테이션이 붙은 파라미터를 처리하는 Argument Resolver입니다.
 *
 * 이 클래스는 세션에서 신규가입할 유저의 "email" 속성을 가져와 해당 파라미터에 주입합니다.
 */
@Component
@RequiredArgsConstructor
public class NewUserEmailArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isNewUserEmailAnnotation =
                parameter.getParameterAnnotation(NewUserEmail.class) != null;
        boolean isString = String.class.equals(parameter.getParameterType());
        return isNewUserEmailAnnotation && isString;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("email");
    }
}
