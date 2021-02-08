package org.prebid.pg.dimval.api.config.app;

import org.prebid.pg.dimval.api.security.CustomBasicAuthEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private static final String REGEX = "\\s*,[,\\s]*";

    @Autowired
    private ServerAuthConfig serverAuthDataConfiguration;

    @Autowired
    private CustomBasicAuthEntryPoint authenticationEntryPoint;

    @Value("${services.base-url}")
    private String baseUrl;

    @Value("${server-api-roles.upload}")
    private String uploadRoles;

    @Value("${server-api-roles.download}")
    private String downloadRoles;

    @Value("${server-api-roles.query}")
    private String queryRoles;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        logger.info("serverAuthDataConfiguration={}", serverAuthDataConfiguration);
        for (final ServerAuthConfig.Principal p : serverAuthDataConfiguration.getPrincipals()) {
            auth.inMemoryAuthentication()
                    .withUser(p.username).password(passwordEncoder().encode(p.password))
                    .roles(p.roles.split(REGEX));
        }

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher(baseUrl + "/v1/attr/upload")
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, baseUrl + "/v1/attr/upload")
                    .hasAnyRole(uploadRoles.split(REGEX))
            .and()
                .httpBasic()
            .and()
                .csrf().disable();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }
}
