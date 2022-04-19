package com.gubkina.authorization.config;


import com.gubkina.authorization.models.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
/**
 * Конфигуратор , позволяющий разделить обьект User и получить из него login и password
 */
@Configuration
public class ApplicationWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new userArgumentResolver());
    }

    private class userArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter methodParameter) {
            return methodParameter.getParameterType().isAssignableFrom(User.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            String login = webRequest.getParameter("user");
            String password = webRequest.getParameter("password");
            return new User(login, password);
        }
    }
}
