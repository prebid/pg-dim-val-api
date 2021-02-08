CREATE DATABASE IF NOT EXISTS dimval;

GRANT ALL PRIVILEGES ON dimval.* TO 'dimval'@'%' IDENTIFIED BY 'dimval';
GRANT ALL PRIVILEGES ON dimval.* TO 'dimval'@'localhost' IDENTIFIED BY 'dimval';

USE dimval;

CREATE TABLE `attr_version` (
  `account_id` varchar(16) NOT NULL,
  `attr_id` varchar(128) NOT NULL,
  `attr_type` varchar(32) NOT NULL,
  `attr_display_name` varchar(64) NOT NULL,
  `version_id` varchar(36) NOT NULL,
  `supports_avails` varchar(1) DEFAULT 'N',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`account_id`,`attr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `attr` (
  `version_id` varchar(36) NOT NULL,
  `account_id` varchar(16) NOT NULL,
  `attr_id` varchar(128) NOT NULL,
  `attr_link_value` varchar(128) NOT NULL,
  `attr_type` varchar(32) NOT NULL,
  `attr_display_name` varchar(64) NOT NULL,
  `value_set` json NOT NULL,
  `supports_avails` varchar(1) DEFAULT 'N',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`version_id`,`account_id`,`attr_id`,`attr_link_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

