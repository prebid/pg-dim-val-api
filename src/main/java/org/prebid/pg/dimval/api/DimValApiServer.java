package org.prebid.pg.dimval.api;

import org.prebid.pg.dimval.api.config.app.CorsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SuppressWarnings("checkstyle:hideutilityclassconstructor")
@SpringBootApplication
public class DimValApiServer {

    private static final Logger logger = LoggerFactory.getLogger(DimValApiServer.class);

    @Autowired
    private CorsConfig corsConfiguration;

    public static void main(String[] args) {
        logger.info("Starting application ...");
        SpringApplication.run(DimValApiServer.class, args);
        logger.debug("Application ready");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                logger.info("CORS::pathPattern={}", corsConfiguration.getPathPattern());
                logger.info("CORS::isAllowCredentials={}", corsConfiguration.isAllowCredentials());
                logger.info("CORS::allowHeaders={}", String.join(",", corsConfiguration.getAllowHeaders()));
                logger.info("CORS::allowOrigins={}", String.join(",", corsConfiguration.getAllowOrigins()));
                logger.info("CORS::allowMethods={}", String.join(",", corsConfiguration.getAllowMethods()));
                logger.info("CORS::maxAge={}", corsConfiguration.getMaxAgeSec());

                registry.addMapping(corsConfiguration.getPathPattern())
                        .allowCredentials(corsConfiguration.isAllowCredentials())
                        .allowedHeaders(corsConfiguration.getAllowHeaders())
                        .allowedOrigins(corsConfiguration.getAllowOrigins())
                        .allowedMethods(corsConfiguration.getAllowMethods())
                        .maxAge(corsConfiguration.getMaxAgeSec());

            }
        };
    }
}
