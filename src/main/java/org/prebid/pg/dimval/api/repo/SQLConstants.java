package org.prebid.pg.dimval.api.repo;

public class SQLConstants {

    public static final String ATTR_VERSION_ORDER_BY
            = "order by account_id, attr_id";

    public static final String ATTR_ORDER_BY
            = "order by version_id, account_id, attr_id, attr_link_value, attr_display_name";

    public static final String GET_ALL_ATTR_LIST
            = "SELECT * FROM attr_version " + ATTR_VERSION_ORDER_BY;

    public static final String GET_ALL_SUPPORTING_PG_AVAILS_ATTR_LIST
            = "SELECT * FROM attr_version WHERE supports_avails = 'Y' " + ATTR_VERSION_ORDER_BY;

    public static final String GET_ATTR_LIST
            = "SELECT * FROM attr_version WHERE account_id = :account_id " + ATTR_VERSION_ORDER_BY;

    public static final String GET_SUPPORTING_PG_AVAILS_ATTR_LIST
            = "SELECT * FROM attr_version "
                + "WHERE account_id = :account_id AND supports_avails = 'Y'" + ATTR_VERSION_ORDER_BY;

    public static final String GET_VERSION_FOR_ATTR_IN_ACCOUNT_REGEXP
            = "SELECT * FROM attr_version WHERE account_id = :account_id AND attr_id REGEXP :attr_id "
                    + ATTR_VERSION_ORDER_BY;

    public static final String GET_VERSION_FOR_ATTR_IN_ACCOUNT_EXACT
            = "SELECT * FROM attr_version WHERE account_id = :account_id AND attr_id = :attr_id "
                    + ATTR_VERSION_ORDER_BY;

    public static final String SELECT_ALL_FROM_ATTR = "SELECT * FROM attr ";

    public static final String GET_ATTR_VALUE_SET
            = SELECT_ALL_FROM_ATTR
            +
            "WHERE account_id = :account_id AND version_id = :version_id "
            +
            "AND "
            +
            "attr_id = :attr_id AND attr_link_value = 'none' " + ATTR_ORDER_BY;

    public static final String GET_SUPPORTING_PG_AVAILS_ATTR_VALUE_SET
            = SELECT_ALL_FROM_ATTR
            +
            "WHERE account_id = :account_id AND version_id = :version_id "
            +
            "AND "
            +
            "attr_id = :attr_id AND attr_link_value = 'none' "
            +
            "supports_avails = 'Y' " + ATTR_ORDER_BY;

    public static final String GET_ATTR_VALUE_SET_BY_SEARCH_STRING
            = SELECT_ALL_FROM_ATTR
            +
            "WHERE "
            +
            "account_id = :account_id AND version_id = :version_id "
            +
            "AND "
            +
            "attr_id = :attr_id "
            +
            "AND "
            +
            "upper(value_set) like :value_set "
            +
            ATTR_ORDER_BY;

    public static final String GET_ATTR_VALUE_SET_BY_ATTR_LINK_VALUE
            = SELECT_ALL_FROM_ATTR
            +
            "WHERE "
            +
            "account_id = :account_id AND version_id = :version_id "
            +
            "AND "
            +
            "attr_id = :attr_id AND attr_link_value = :attr_link_value "
            +
            ATTR_ORDER_BY;

    public static final String INSERT_ATTR
            = "INSERT INTO attr "
            +
            "(version_id, account_id, attr_id, attr_link_value, attr_type, attr_display_name, value_set) "
            +
            "VALUES (:version_id, :account_id, :attr_id, :attr_link_value, :attr_type, :attr_display_name, :value_set)";

    public static final String INSERT_ATTR_V2 = "INSERT INTO attr "
            +
            "("
            +
            "version_id, account_id, attr_id, attr_link_value, "
            +
            "attr_type, attr_display_name, value_set, supports_avails"
            +
            ") "
            +
            "VALUES ("
            +
            ":version_id, :account_id, :attr_id, :attr_link_value, "
            +
            ":attr_type, :attr_display_name, :value_set, :supports_avails"
            +
            ")";

    public static final String REPLACE_ATTR_VERSION
            = "REPLACE INTO attr_version "
            +
            "(account_id, attr_id, attr_type, attr_display_name, version_id, supports_avails)"
            +
            "VALUES (:account_id, :attr_id, :attr_type, :attr_display_name, :version_id, :supports_avails)";

    public static final String DELETE_EXPIRED_VERSION
            = "DELETE FROM attr "
            +
            "WHERE version_id != :version_id AND account_id = :account_id AND attr_id = :attr_id";

    public static final String GET_VALUE_COUNTS_BY_ATTR_AND_ACCOUNT
            = "SELECT SUM(JSON_LENGTH(value_set)) FROM attr "
            +
            "WHERE attr_id = :attr_id AND account_id = :account_id";

    public static final String GET_SUPPORTING_PG_AVAILS_COUNTS_BY_ATTR_AND_ACCOUNT
            = "SELECT count(*) FROM attr "
            +
            "WHERE attr_id = :attr_id AND account_id = :account_id AND supports_avails = \'Y\'";

    private SQLConstants() {
        throw new IllegalStateException("Utility class");
    }
}
