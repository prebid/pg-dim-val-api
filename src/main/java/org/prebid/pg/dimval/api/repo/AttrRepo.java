package org.prebid.pg.dimval.api.repo;

import org.prebid.pg.dimval.api.persistence.AttrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AttrRepo extends JpaRepository<AttrEntity, Long> {

    @Query(value = SQLConstants.GET_ATTR_VALUE_SET, nativeQuery = true)
    @QueryHints(value = {
            @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"),
            @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")}
    )
    AttrEntity getAttrValues(
            @Param("account_id") String accountId,
            @Param("version_id") String versionId,
            @Param("attr_id") String attrId
    );

    @Query(value = SQLConstants.GET_SUPPORTING_PG_AVAILS_ATTR_VALUE_SET, nativeQuery = true)
    @QueryHints(value = {
            @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"),
            @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")}
    )
    AttrEntity getSupportingPGAvailsAttrValues(
            @Param("account_id") String accountId,
            @Param("version_id") String versionId,
            @Param("attr_id") String attrId
    );

    @Query(value = SQLConstants.GET_ATTR_VALUE_SET_BY_SEARCH_STRING, nativeQuery = true)
    @QueryHints(value = {
            @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"),
            @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")}
    )
    List<AttrEntity> getAttrBySearchString(
            @Param("account_id") String accountId,
            @Param("version_id") String versionId,
            @Param("attr_id") String attrId,
            @Param("value_set") String valueSet
    );

    @Query(value = SQLConstants.GET_ATTR_VALUE_SET_BY_ATTR_LINK_VALUE, nativeQuery = true)
    @QueryHints(value = {
            @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"),
            @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")}
    )
    AttrEntity getAttrValuesByLink(
            @Param("account_id") String accountId,
            @Param("version_id") String versionId,
            @Param("attr_id") String attrId,
            @Param("attr_link_value") String attrLinkValue
    );

    @Modifying
    @Transactional
    @Query(value = SQLConstants.INSERT_ATTR, nativeQuery = true)
    @QueryHints(value = {
            @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")}
    )
    void uploadAttrRow(
            @Param("version_id") String versionId,
            @Param("account_id") String accountId,
            @Param("attr_id") String attrId,
            @Param("attr_link_value") String attrLinkValue,
            @Param("attr_type") String attrType,
            @Param("attr_display_name") String attrDisplayName,
            @Param("value_set") String valueSet
    );

    @Modifying
    @Transactional
    @Query(value = SQLConstants.INSERT_ATTR_V2, nativeQuery = true)
    @QueryHints(value = {
            @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")}
    )
    void uploadAttrRow(
            @Param("version_id") String versionId,
            @Param("account_id") String accountId,
            @Param("attr_id") String attrId,
            @Param("attr_link_value") String attrLinkValue,
            @Param("attr_type") String attrType,
            @Param("attr_display_name") String attrDisplayName,
            @Param("value_set") String valueSet,
            @Param("supports_avails") String supportsAvails
    );

    @Modifying
    @Transactional
    @Query(value = SQLConstants.DELETE_EXPIRED_VERSION, nativeQuery = true)
    @QueryHints(value = {
            @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")}
    )
    void deleteExpiredVersion(
            @Param("version_id") String versionId,
            @Param("account_id") String accountId,
            @Param("attr_id") String attrId
    );

    @Query(value = SQLConstants.GET_VALUE_COUNTS_BY_ATTR_AND_ACCOUNT, nativeQuery = true)
    @QueryHints(value = {
            @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")}
    )
    Integer getValueCountsByAttrAndAccount(
            @Param("attr_id") String attrId,
            @Param("account_id") String accountId
    );

    @Query(value = SQLConstants.GET_SUPPORTING_PG_AVAILS_COUNTS_BY_ATTR_AND_ACCOUNT, nativeQuery = true)
    @QueryHints(value = {
            @javax.persistence.QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @javax.persistence.QueryHint(name = "javax.persistence.query.timeout", value = "15000")}
    )
    Integer getValueCountsBySupportingPGAvailsAttrAndAccount(
            @Param("attr_id") String attrId,
            @Param("account_id") String accountId
    );
}
