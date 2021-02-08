package org.prebid.pg.dimval.api.config.app;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Data
@ToString
@ConfigurationProperties(prefix = "services")
public class AttrDataConfig {

    List<AttrTreeLink> attrTreeLinks = new ArrayList<>();

    @Data
    @ToString
    public static class AttrTreeLink {

        String parent;

        String leaf;

    }

}
