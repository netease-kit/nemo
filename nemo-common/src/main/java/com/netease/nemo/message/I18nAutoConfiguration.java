package com.netease.nemo.message;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;

@Configuration
public class I18nAutoConfiguration {

    @Bean("messageSource")
    public MessageSource MessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:restMessage/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600);
        messageSource.setAlwaysUseMessageFormat(true);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new I18nAutoConfiguration.I18nCookieLocaleResolver();
        cookieLocaleResolver.setCookieName("locale");
        cookieLocaleResolver.setDefaultLocale(Locale.CHINA);
        return cookieLocaleResolver;
    }

    class I18nCookieLocaleResolver extends CookieLocaleResolver {
        @Override
        protected Locale parseLocaleValue(String locale) {
            return locale == null ? this.getDefaultLocale() : StringUtils.parseLocaleString(locale.replace("-", "_"));
        }
    }
}
