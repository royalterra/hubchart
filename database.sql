DROP TABLE IF EXISTS global_stats;
DROP TABLE IF EXISTS hub_stats;
DROP TABLE IF EXISTS hubs;
CREATE TABLE hubs (
	id int NOT NULL auto_increment,
	base_url varchar(512) NOT NULL,
	poll_url varchar(512) NOT NULL,
	description varchar(1024),
	logo_url varchar(1024),
	name varchar(256),
	ip_address varchar(64),
	country_code varchar(4),
	network_type varchar(32) NOT NULL,
	registration_policy varchar(4),
	version varchar(32),
	expired bit NOT NULL,
	poll_time timestamp NOT NULL,
	id_last_hub_stats int NOT NULL,
	PRIMARY KEY(id)
);
CREATE TABLE hub_stats (
	id int NOT NULL auto_increment,
	poll_time timestamp NOT NULL,
	total_channels int,
	active_channels_last_month int,
	active_channels_last_6_months int,
	total_posts int,
	id_registered_hub int NOT NULL,
	PRIMARY KEY(id)
);
CREATE TABLE image_cache (
	id int NOT NULL auto_increment,
	name varchar(128),
	image mediumblob NOT NULL,
	mime_type varchar(128) NOT NULL,
	id_registered_hub int,
	update_time timestamp NOT NULL,
	PRIMARY KEY(id)
);
ALTER TABLE hub_stats ADD CONSTRAINT fk_registered_hub FOREIGN KEY (id_registered_hub) REFERENCES hubs(id);
CREATE INDEX idx_registered_hub ON hub_stats(id_registered_hub);
CREATE TABLE global_stats (
	id int NOT NULL auto_increment,
	poll_time timestamp,
	total_channels int,
	active_channels_last_month int,
	active_channels_last_6_months int,
	total_posts int,
	PRIMARY KEY(id)
);
alter table hubs change poll_url fqdn varchar(512);
alter table hubs change description info varchar(1024);
alter table hubs change logo_url plugins varchar(1024);
alter table hubs add column feature_diaspora bit NOT NULL;
alter table hubs add column feature_rss bit NOT NULL;
alter table hubs drop column fqdn;
alter table hubs add column country_name varchar(256);
alter table hubs add column poll_failed bit NOT NULL DEFAULT FALSE;
create index IDX_HUB_BASE_URL on hubs(base_url);
alter table hubs change feature_diaspora feature_diaspora bit NOT NULL DEFAULT FALSE;
alter table hubs change feature_rss feature_rss bit NOT NULL DEFAULT FALSE;
alter table image_cache change id_registered_hub id_stat int NOT NULL;
alter table image_cache change update_time update_time datetime NOT NULL;
alter table hubs change poll_time poll_time datetime NOT NULL;
alter table hub_stats change poll_time poll_time datetime NOT NULL;
alter table global_stats change poll_time poll_time datetime NOT NULL;
rename table hub_stats TO statistics;
alter table statistics drop foreign key fk_registered_hub;
alter table statistics change id_registered_hub id_hub int DEFAULT NULL;
alter table image_cache drop column name;
drop table global_stats;
alter table hubs add column creation_time datetime DEFAULT NULL;
alter table hubs add column directory_mode  varchar(4) DEFAULT NULL;
alter table hubs change plugins plugins varchar(2048);
alter table hubs add column id_language int DEFAULT NULL;
alter table statistics add column active_hubs int DEFAULT NULL;
alter table image_cache add column chart_type varchar(4) NOT NULL;
alter table image_cache add column title varchar(128) DEFAULT NULL;
alter table hubs add column admin_name varchar(512) DEFAULT NULL;
alter table hubs add column admin_address varchar(512) DEFAULT NULL;
alter table hubs add column admin_channel varchar(512) DEFAULT NULL;
alter table hubs add column hidden bit NOT NULL DEFAULT FALSE;
alter table hubs add column fqdn varchar(512) NOT NULL;
alter table hubs add column version_tag varchar(32) NOT NULL;
alter table hubs change poll_time last_successful_poll_time timestamp NOT NULL;
alter table hubs add column deleted bit NOT NULL DEFAULT FALSE;
alter table hubs drop column expired;
alter table hubs drop column poll_failed;
alter table hubs change version_tag version_tag varchar(32) DEFAULT NULL;
CREATE TABLE feed_entries (
	id int NOT NULL auto_increment,
	title varchar(1024) NOT NULL,
	link varchar(1024) NOT NULL,
	published_date timestamp,
	description_type varchar(256) NOT NULL,
	description_value text NOT NULL,
	PRIMARY KEY(id)
);
