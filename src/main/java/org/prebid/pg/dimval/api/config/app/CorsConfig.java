package org.prebid.pg.dimval.api.config.app;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "services.cors")
@Data
public class CorsConfig {
    private boolean enabled;

    private String pathPattern;

    private boolean allowCredentials;

    private String[] allowHeaders;

    private String[] allowOrigins;

    private String[] allowMethods;

    private long maxAgeSec;
}
