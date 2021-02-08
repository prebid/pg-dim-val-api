package org.prebid.pg.dimval.api.config.app;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.List;

@Configuration
@Data
@ToString
@ConfigurationProperties(prefix = "server-auth")
public class ServerAuthConfig {

    String realm;

    Boolean enabled;

    List<Principal> principals;

    @Data
    public static class Principal {

        @NotNull
        String username;

        @NotNull
        String password;

        @NotNull
        String roles;

        public String toString() {

            return String.format("username=%s, roles=%s", username, roles);
        }

    }

}
