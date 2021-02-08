package org.prebid.pg.dimval.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "services.cors.max-age-sec=60"
})
public class DimValApiServerTest {

    @Test
    public void shouldLoadContextCleanly() {
        assert true;
    }

}
