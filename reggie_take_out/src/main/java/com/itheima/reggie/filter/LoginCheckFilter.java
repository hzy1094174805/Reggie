package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录检查过滤器
 *
 * @author ilovend
 * @date 2023/02/16
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    /**
     * 路径匹配器
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 初始化
     *
     * @param config 配置
     * @throws ServletException servlet异常
     */
    public void init(FilterConfig config) throws ServletException {
    }

    /**
     * 摧毁
     */
    public void destroy() {
    }

    /**
     * 做过滤器
     *
     * @param request  请求
     * @param response 响应
     * @param chain    链
     * @throws ServletException servlet异常
     * @throws IOException      ioexception
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        //1.获取请求路径
        String requestURI = req.getRequestURI();

        log.info("请求路径：{}", requestURI);
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
        };
        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3.如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            chain.doFilter(req, resp);
            return;
        }
        //4.判断登录状态，如已经登录，则直接放行
        if (req.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，用户id为{}",req.getSession().getAttribute("employee"));

            Long empId = (Long) req.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            chain.doFilter(req, resp);
            return;
        }

        if (req.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为{}", req.getSession().getAttribute("user"));
            Long userId = (Long) req.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            chain.doFilter(req, resp);
            return;
        }
        log.info("用户未登录，跳转到登录页面");
        resp.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 检查
     *
     * @param urls       url
     * @param requestURI 请求uri
     * @return boolean
     */
    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
