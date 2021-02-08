package org.prebid.pg.dimval.api.repo;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AttrRepoTest {
    SoftAssertions softAssertions;

    @Autowired
    AttrRepo attrRepo;

    @BeforeEach
    void init() {
        softAssertions = new SoftAssertions();
    }

    @Test
    public void attrRepoTests() {
        softAssertions.assertThatCode(() -> attrRepo.uploadAttrRow(
                "versionId", "accountId1", "attrId", "none",
                "attrType", "attrDisplayName", "valueSet"
        )).doesNotThrowAnyException();

        softAssertions.assertThatCode(() -> attrRepo.uploadAttrRow(
                "versionId", "accountId2", "attrId", "none",
                "attrType", "attrDisplayName", "valueSet", "N"
        )).doesNotThrowAnyException();

        softAssertions.assertThat(attrRepo.getAttrValues("accountId2", "versionId", "attrId"))
                .hasFieldOrPropertyWithValue("attrDisplayName", "attrDisplayName");

        softAssertions.assertThat(attrRepo.getAttrBySearchString(
                "accountId2","versionId","attrId","%VALUE%"
        )).hasSize(1);

        softAssertions.assertThat(attrRepo.getAttrValuesByLink(
                "accountId2","versionId","attrId","none"
        )).hasFieldOrPropertyWithValue("attrDisplayName", "attrDisplayName");

        softAssertions.assertThatCode(() -> attrRepo.deleteExpiredVersion(
                "versionId", "accountId2", "attrId"
        )).doesNotThrowAnyException();

        softAssertions.assertAll();
    }

}