package com.lhsdev.cmsproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.time.Duration;
import java.util.Locale;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        // Explicitly naming the bean 'localeResolver' is CRITICAL for Spring to
        // recognize it.
        CookieLocaleResolver clr = new CookieLocaleResolver("SITE_LANG");
        clr.setDefaultLocale(Locale.KOREAN);
        clr.setCookieMaxAge(Duration.ofDays(31));
        clr.setCookiePath("/");
        return clr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // URL parameter like ?lang=en
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the interceptor to watch for the 'lang' parameter on all requests.
        registry.addInterceptor(localeChangeInterceptor());
    }
}
