package org.prebid.pg.dimval.api.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prebid.pg.dimval.api.config.app.ServerAuthConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomBasicAuthEntryPointTest {

    SoftAssertions softAssertions;

    @BeforeEach
    public void setup() {
        softAssertions = new SoftAssertions();
    }

    @Test
    void commence() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticationException authenticationException =
                new AuthenticationCredentialsNotFoundException("security problem");

        CustomBasicAuthEntryPoint customBasicAuthEntryPoint = new CustomBasicAuthEntryPoint();
        customBasicAuthEntryPoint.setRealmName(getTestRealmName());

        softAssertions
                .assertThatCode(() -> customBasicAuthEntryPoint.commence(request, response, authenticationException))
                .doesNotThrowAnyException();

        softAssertions.assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);

        softAssertions.assertThat(response.getHeaderValue("WWW-Authenticate")).isEqualTo("Basic realm=\"" + getTestRealmName() + "\"");

        softAssertions.assertAll();
    }


    private String getTestRealmName() {
        return "dim-val-realm";
    }
}