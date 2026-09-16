package com.xinji.config;

import com.xinji.util.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;

    public WebConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor(jwtUtil))
                .addPathPatterns("/api/parts/**", "/api/trace-records/**", "/api/dashboard/**")
                .excludePathPatterns("/api/parts/query/**")
                .excludePathPatterns("/api/trace-records/part/**");
    }

    static class JwtInterceptor implements HandlerInterceptor {
        private final JwtUtil jwtUtil;

        JwtInterceptor(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                 Object handler) throws Exception {
            // OPTIONS 预检请求放行
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                return true;
            }

            // GET 请求：part query 和 trace-records 允许公开访问
            String path = request.getRequestURI();
            String method = request.getMethod();

            // GET /api/parts/query/* 和 GET /api/trace-records/part/* 放行
            if ("GET".equalsIgnoreCase(method)) {
                if (path.contains("/api/parts/query/") || path.contains("/api/trace-records/part/")) {
                    return true;
                }
                // GET /api/parts/{id} 也放行（消费者查看详情）
                if (path.matches(".*/api/parts/\\d+$")) {
                    return true;
                }
            }

            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(401);
                response.getWriter().write("{\"code\":401,\"message\":\"未登录或 token 已过期\"}");
                return false;
            }

            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                if (username == null || !jwtUtil.validateToken(token)) {
                    throw new Exception("Invalid token");
                }
                request.setAttribute("username", username);
                request.setAttribute("role", jwtUtil.getRoleFromToken(token));
                return true;
            } catch (Exception e) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(401);
                response.getWriter().write("{\"code\":401,\"message\":\"token 无效或已过期\"}");
                return false;
            }
        }
    }
}
