package com.wordle.common.interceptor;

import com.wordle.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req,
                             HttpServletResponse resp,
                             Object handler) throws Exception {

        var loginUser = (User) req.getSession().getAttribute("loginUser");

        // 로그인 안했거나 관리자 아니면 차단
        if (loginUser == null || !loginUser.isAdmin()) {
            resp.sendRedirect("/");
            return false;
        }

        return true;
    }
}
