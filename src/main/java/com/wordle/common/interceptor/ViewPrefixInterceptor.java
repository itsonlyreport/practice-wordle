package com.wordle.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ViewPrefixInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest req,
                           HttpServletResponse resp,
                           Object handler,
                           ModelAndView mav) {

        if (mav == null || mav.getViewName() == null) return;
        if (mav.getViewName().startsWith("redirect:")) return;
        if (mav.getViewName().startsWith("admin/")) return;
        if (mav.getViewName().startsWith("user/")) return;

        var viewName = mav.getViewName();
        var uri      = req.getRequestURI();

        // /admin/** 요청이면 admin/ prefix 추가
        if (uri.startsWith("/admin/")) {
            mav.setViewName("admin/" + viewName);
        } else {
            // 나머지는 user/ prefix 추가
            mav.setViewName("user/" + viewName);
        }
    }
}
