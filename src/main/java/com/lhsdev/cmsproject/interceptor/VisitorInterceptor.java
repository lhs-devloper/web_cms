package com.lhsdev.cmsproject.interceptor;

import com.lhsdev.cmsproject.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class VisitorInterceptor implements HandlerInterceptor {

    private final VisitorService visitorService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Exclude specific requests if needed (e.g. static resources are usually
        // handled by resource handlers and might not hit here depending on config, but
        // API calls might)
        // Here we rely on WebConfig exclusion patterns for static resources.

        HttpSession session = request.getSession(true);

        // Check if this session has already been logged today
        if (session.getAttribute("VISITOR_LOGGED") == null) {
            String ip = getClientIp(request);
            String agent = request.getHeader("User-Agent");
            String uri = request.getRequestURI();

            visitorService.logVisit(ip, agent, uri);

            // Mark session as logged
            session.setAttribute("VISITOR_LOGGED", true);
            // Optional: Set expiration or check date match if long-lived sessions are a
            // concern.
            // For simple stats, session-based UV is standard.
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
