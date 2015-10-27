--
-- Table structure for table `image_cache`
--
DROP TABLE IF EXISTS `image_cache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `image_cache` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image` mediumblob NOT NULL,
  `mime_type` varchar(128) NOT NULL,
  `id_stat` int(11) NOT NULL,
  `update_time` datetime NOT NULL,
  `chart_type` varchar(4) NOT NULL,
  `title` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Table structure for table `feed_entries`
--
DROP TABLE IF EXISTS `feed_entries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feed_entries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(1024) NOT NULL,
  `link` varchar(1024) NOT NULL,
  `published_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description_type` varchar(256) NOT NULL,
  `description_value` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Table structure for table `settings`
--
DROP TABLE IF EXISTS `settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `value` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_settings_name` (`name`),
  KEY `idx_settings_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Table structure for table `statistics`
--
DROP TABLE IF EXISTS `statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `poll_time` datetime NOT NULL,
  `total_channels` int(11) DEFAULT NULL,
  `active_channels_last_month` int(11) DEFAULT NULL,
  `active_channels_last_6_months` int(11) DEFAULT NULL,
  `total_posts` int(11) DEFAULT NULL,
  `id_hub` int(11) DEFAULT NULL,
  `active_hubs` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_registered_hub` (`id_hub`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Table structure for table `hubs`
--
DROP TABLE IF EXISTS `hubs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hubs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `base_url` varchar(512) NOT NULL,
  `info` varchar(1024) DEFAULT NULL,
  `plugins` varchar(2048) DEFAULT NULL,
  `name` varchar(256) DEFAULT NULL,
  `ip_address` varchar(64) DEFAULT NULL,
  `country_code` varchar(4) DEFAULT NULL,
  `network_type` varchar(32) NOT NULL,
  `registration_policy` varchar(4) DEFAULT NULL,
  `version` varchar(32) DEFAULT NULL,
  `last_successful_poll_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `id_last_hub_stats` int(11) NOT NULL,
  `country_name` varchar(256) DEFAULT NULL,
  `creation_time` datetime DEFAULT NULL,
  `directory_mode` varchar(4) DEFAULT NULL,
  `id_language` int(11) DEFAULT NULL,
  `admin_name` varchar(512) DEFAULT NULL,
  `admin_address` varchar(512) DEFAULT NULL,
  `admin_channel` varchar(512) DEFAULT NULL,
  `hidden` bit(1) NOT NULL DEFAULT b'0',
  `fqdn` varchar(512) NOT NULL,
  `version_tag` varchar(32) DEFAULT NULL,
  `poll_queue` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_hubs_base_url` (`base_url`),
  KEY `idx_hubs_poll_queue` (`poll_queue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
-- Table structure for table `image_cache`
--
DROP TABLE IF EXISTS `logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `level` varchar(16) DEFAULT NULL,
  `time` datetime NOT NULL,
  `message` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;