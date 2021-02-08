CREATE TABLE IF NOT EXISTS `attr_version` (
  `account_id` varchar(16) NOT NULL,
  `attr_id` varchar(128) NOT NULL DEFAULT '',
  `attr_type` varchar(32) NOT NULL DEFAULT '',
  `version_id` varchar(36) NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `attr_display_name` varchar(64) NOT NULL DEFAULT '',
  `supports_avails` varchar(1) DEFAULT 'Y',
PRIMARY KEY (`account_id`,`attr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `attr` (
  `version_id` varchar(36) NOT NULL,
  `account_id` varchar(16) NOT NULL,
  `attr_id` varchar(128) NOT NULL,
  `attr_link_value` varchar(128) NOT NULL,
  `attr_type` varchar(32) NOT NULL DEFAULT '',
  `attr_display_name` varchar(64) NOT NULL,
  `value_set` varchar(64) NOT NULL,
  `supports_avails` varchar(1) DEFAULT 'Y',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`version_id`,`account_id`,`attr_id`,`attr_link_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
