package org.prebid.pg.dimval.api.repo;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.EntityManager;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.prebid.pg.dimval.api.persistence.AttrVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class AttrVersionRepoTest {

    SoftAssertions softAssertions;

    @Autowired
    AttrVersionRepo attrVersionRepo;

    @Autowired
    EntityManager entityManager;

    private String attrId = "attrId";
    private String accountId = "accountId";
    private String attrType = "attrType";
    private String versionId = "versionId";
    private String attrDisplayName = "attrDisplayName";

    @BeforeEach
    void setUp() {
        softAssertions = new SoftAssertions();
    }

    @AfterEach
    public void clean() {
        entityManager.clear();
    }

    @Test
    public void shouldLoadContextCleanly() {
        softAssertions.assertThat(entityManager).isNotNull();
        softAssertions.assertThat(attrVersionRepo).isNotNull();
        softAssertions.assertAll();
    }

    @Test
    @Transactional
    public void getAllAttrListWithVersion() {
        Timestamp now = Timestamp.from(Instant.now());

        for (int i=0; i<3; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId + i);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setUpdatedAt(now);
            entityManager.persist(attrVersion);
        }

        List<AttrVersion> versions = attrVersionRepo.getAllAttrList();
        versions.sort(new AttrVersionSorter());

        softAssertions.assertThat(versions).hasSize(3);
        softAssertions.assertThat(versions.get(0)).isInstanceOf(AttrVersion.class);
        softAssertions.assertThat(versions.get(0)).hasFieldOrPropertyWithValue("accountId", "accountId0");
        softAssertions.assertThat(versions.get(1)).hasFieldOrPropertyWithValue("accountId", "accountId1");
        softAssertions.assertThat(versions.get(2)).hasFieldOrPropertyWithValue("accountId", "accountId2");

        softAssertions.assertAll();
    }

    @Test
    @Transactional
    public void getAttrListWithVersion() {
        Timestamp now = Timestamp.from(Instant.now());

        for (int i=0; i<3; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setUpdatedAt(now);
            entityManager.persist(attrVersion);
        }

        List<AttrVersion> versions = attrVersionRepo.getAttrList(accountId);
        versions.sort(new AttrVersionSorter());

        softAssertions.assertThat(versions).hasSize(3);
        softAssertions.assertThat(versions.get(0)).isInstanceOf(AttrVersion.class);
        softAssertions.assertThat(versions.get(0)).hasFieldOrPropertyWithValue("versionId", versionId + "0");
        softAssertions.assertThat(versions.get(1)).hasFieldOrPropertyWithValue("versionId", versionId + "1");
        softAssertions.assertThat(versions.get(2)).hasFieldOrPropertyWithValue("versionId", versionId + "2");

        softAssertions.assertAll();
    }

    @Test
    @Transactional
    public void getVersionForAttrInAccountExact() {
        Timestamp now = Timestamp.from(Instant.now());

        for (int i=0; i<3; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setUpdatedAt(now);
            entityManager.persist(attrVersion);
        }

        AttrVersion version = attrVersionRepo.getVersionForAttrInAccountExact(attrId + "1", accountId);
        softAssertions.assertThat(version).isInstanceOf(AttrVersion.class);
        softAssertions.assertThat(version).hasFieldOrPropertyWithValue("versionId", versionId + "1");

        softAssertions.assertAll();
    }

    @Test
    @Transactional
    public void getVersionForAttrInAccountRegexp() {
        Timestamp now = Timestamp.from(Instant.now());

        for (int i=0; i<3; i++) {
            AttrVersion attrVersion = new AttrVersion();
            attrVersion.setAccountId(accountId);
            attrVersion.setAttrId(attrId + i);
            attrVersion.setAttrType(attrType + i);
            attrVersion.setVersionId(versionId + i);
            attrVersion.setAttrDisplayName(attrDisplayName + i);
            attrVersion.setUpdatedAt(now);
            entityManager.persist(attrVersion);
        }

        List<AttrVersion> versions = attrVersionRepo.getVersionForAttrInAccountRegexp("ttrId", accountId);
        softAssertions.assertThat(versions).isInstanceOf(ArrayList.class);
        versions.sort(new AttrVersionSorter());

        softAssertions.assertThat(versions).hasSize(3);
        softAssertions.assertThat(versions.get(0)).isInstanceOf(AttrVersion.class);
        softAssertions.assertThat(versions.get(0)).hasFieldOrPropertyWithValue("attrId", attrId + "0");
        softAssertions.assertThat(versions.get(1)).hasFieldOrPropertyWithValue("attrId", attrId + "1");
        softAssertions.assertThat(versions.get(2)).hasFieldOrPropertyWithValue("attrId", attrId + "2");

        softAssertions.assertAll();
    }

    @Test
    @Transactional
    public void updateVersion() {
        //REPLACE partially supported in h2
        attrVersionRepo.updateVersion(accountId, attrId, attrType, attrDisplayName, versionId, "Y");
        List<AttrVersion> versions = attrVersionRepo.getAllAttrList();

        softAssertions.assertThat(versions).hasSize(1);
        softAssertions.assertThat(versions.get(0)).isInstanceOf(AttrVersion.class);
        softAssertions.assertThat(versions.get(0)).hasFieldOrPropertyWithValue("versionId", versionId);

        softAssertions.assertAll();
    }
}

class AttrVersionSorter implements Comparator<AttrVersion> {
    @Override
    public int compare(AttrVersion o1, AttrVersion o2) {
        return o1.getAttrId().compareTo(o2.getAttrId());
    }
}