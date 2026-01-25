package com.lhsdev.cmsproject.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SessionListener implements HttpSessionListener {

    private static final AtomicInteger ACTIVE_SESSIONS = new AtomicInteger(0);

    public static int getActiveSessions() {
        return ACTIVE_SESSIONS.get();
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("session 생성");
        ACTIVE_SESSIONS.incrementAndGet();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("session 파괴");
        ACTIVE_SESSIONS.decrementAndGet();
    }
}
