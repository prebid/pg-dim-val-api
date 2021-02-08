package org.prebid.pg.dimval.api.security;

import org.prebid.pg.dimval.api.config.app.ServerAuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomBasicAuthEntryPoint extends BasicAuthenticationEntryPoint {

    @Autowired
    private ServerAuthConfig serverAuthDataConfiguration;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx
    ) throws IOException {
        response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println("HTTP Status 401 - " + authEx.getMessage());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName(serverAuthDataConfiguration.getRealm());
        super.afterPropertiesSet();
    }
}
