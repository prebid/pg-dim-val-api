package org.prebid.pg.dimval.api.repo;

import org.prebid.pg.dimval.api.persistence.AttrVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AttrVersionRepo extends JpaRepository<AttrVersion, Long> {

    @Query(value = SQLConstants.GET_ALL_ATTR_LIST, nativeQuery = true)
    @QueryHints(
            value = {
                    @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
                    @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")
            }
    )
    List<AttrVersion> getAllAttrList();

    @Query(value = SQLConstants.GET_ATTR_LIST, nativeQuery = true)
    @QueryHints(
            value = {
                @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
                @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")
            }
    )
    List<AttrVersion> getAttrList(@Param("account_id") String accountId);

    @Query(value = SQLConstants.GET_SUPPORTING_PG_AVAILS_ATTR_LIST, nativeQuery = true)
    @QueryHints(
            value = {
                    @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
                    @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")
            }
    )
    List<AttrVersion> getSupportingPGAvailsAttrList(@Param("account_id") String accountId);

    @Query(value = SQLConstants.GET_VERSION_FOR_ATTR_IN_ACCOUNT_REGEXP, nativeQuery = true)
    @QueryHints(
            value = {
                    @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
                    @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")
            }
    )
    List<AttrVersion> getVersionForAttrInAccountRegexp(
            @Param("attr_id") String attrId, @Param("account_id") String accountId
    );

    @Query(value = SQLConstants.GET_VERSION_FOR_ATTR_IN_ACCOUNT_EXACT, nativeQuery = true)
    @QueryHints(
            value = {
                    @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
                    @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")
            }
    )
    AttrVersion getVersionForAttrInAccountExact(@Param("attr_id") String attrId, @Param("account_id") String accountId);

    @Modifying
    @Transactional
    @Query(value = SQLConstants.REPLACE_ATTR_VERSION, nativeQuery = true)
    @QueryHints(
            value = {
                @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")
            }
    )
    void updateVersion(
            @Param("account_id") String accountId,
            @Param("attr_id") String attrId,
            @Param("attr_type") String attrType,
            @Param("attr_display_name") String attrDisplayName,
            @Param("version_id") String versionId,
            @Param("supports_avails") String supportsAvails
    );

}
