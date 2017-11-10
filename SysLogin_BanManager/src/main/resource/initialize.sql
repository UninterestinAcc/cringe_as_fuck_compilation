CREATE TABLE IF NOT EXISTS `Logins` (
    `USERNAME` varchar(16) NOT NULL UNIQUE,
    `PASSWORD_ENCODED` varchar(384) NOT NULL,
    `REGISTER_DATE` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `DISCORD_SNOWFLAKE` varchar(20),
    `VOTES` integer NOT NULL DEFAULT 0,
    `LAST_VOTE` datetime,
    `ONLOGIN_SWITCHTO` varchar(64),
    `REGISTER_IP` varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS `IPs` (
    `USERNAME` varchar(16) NOT NULL,
    `IP` varchar(50) NOT NULL,
    `LAST_USED` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uniqueRelation UNIQUE (`USERNAME`,`IP`)
);

CREATE TABLE IF NOT EXISTS `WhitelistedLoginIPs` (
    `USERNAME` varchar(16) NOT NULL,
    `IP` varchar(50) NOT NULL,
    CONSTRAINT uniqueRelation UNIQUE (`USERNAME`,`IP`)
);

CREATE TABLE IF NOT EXISTS `PasswordChanges` (
    `USERNAME` varchar(16) NOT NULL,
    `IP` varchar(50) NOT NULL,
    `OLD_PASSWORD` varchar(384) NOT NULL,
    `NEW_PASSWORD` varchar(384) NOT NULL,
    `TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uniqueRelation UNIQUE (`USERNAME`,`OLD_PASSWORD`,`TIME`)
);

CREATE TABLE IF NOT EXISTS `Staffs` (
    `USERNAME` varchar(16) NOT NULL UNIQUE,
    `PERMISSIONS` bigint(20) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS `StaffStats` (
    `USERNAME` varchar(16) NOT NULL,
    `LOGIN` datetime NOT NULL,
    `LOGOUT` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `IP` varchar(50) NOT NULL,
    CONSTRAINT uniqueRelation UNIQUE (`USERNAME`,`LOGIN`)
);

CREATE TABLE IF NOT EXISTS `PlayerStatistics` (
    `LOGIN_COUNT` integer NOT NULL,
    `LOGOUT_COUNT` integer NOT NULL,
    `PLAYER_COUNT` integer NOT NULL,
    `HOST` varchar(64) NOT NULL DEFAULT "bungeecord-xx-1",
    `TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uniqueRelation UNIQUE( `HOST`, `TIME`)
);

CREATE TABLE IF NOT EXISTS `UserBans` (
    `USERNAME` varchar(16) NOT NULL,
    `STARTTIME` bigint(20) NOT NULL,
    `ENDTIME` bigint(20) NOT NULL,
    `BY` varchar(16) NOT NULL,
    `LIFTED_BY` varchar(16),
    `REASON` varchar(256) NOT NULL,
    CONSTRAINT uniqueRelation UNIQUE (`USERNAME`,`STARTTIME`)
);

CREATE TABLE IF NOT EXISTS `UserMutes` (
    `USERNAME` varchar(16) NOT NULL,
    `STARTTIME` bigint(20) NOT NULL,
    `ENDTIME` bigint(20) NOT NULL,
    `BY` varchar(16) NOT NULL,
    `LIFTED_BY` varchar(16),
    `REASON` varchar(256) NOT NULL,
    CONSTRAINT uniqueRelation UNIQUE (`USERNAME`,`STARTTIME`)
);

CREATE TABLE IF NOT EXISTS `UserWarns` (
    `USERNAME` varchar(16) NOT NULL,
    `STARTTIME` bigint(20) NOT NULL,
    `BY` varchar(16) NOT NULL,
    `REASON` varchar(256) NOT NULL,
    CONSTRAINT uniqueRelation UNIQUE (`USERNAME`,`STARTTIME`)
);

CREATE TABLE IF NOT EXISTS `IPBans` (
    `IP` varchar(50) NOT NULL,
    `STARTTIME` bigint(20) NOT NULL,
    `ENDTIME` bigint(20) NOT NULL,
    `BY` varchar(16) NOT NULL,
    `LIFTED_BY` varchar(16),
    `REASON` varchar(256) NOT NULL,
    CONSTRAINT uniqueRelation UNIQUE (`IP`,`STARTTIME`)
);

CREATE TABLE IF NOT EXISTS `ISPBans` (
    `ASN` integer NOT NULL,
    `STARTTIME` bigint(20) NOT NULL,
    `ENDTIME` bigint(20) NOT NULL,
    `BY` varchar(16) NOT NULL,
    `LIFTED_BY` varchar(16),
    `REASON` varchar(256) NOT NULL,
    CONSTRAINT uniqueRelation UNIQUE (`ASN`,`STARTTIME`)
);

CREATE TABLE IF NOT EXISTS `MOTD` (
    `MESSAGE` varchar(512) NOT NULL,
    `CREATETIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP UNIQUE
);

CREATE TABLE IF NOT EXISTS `VoteSites` (
    `SITE_NUMBER` integer NOT NULL AUTO_INCREMENT UNIQUE,
    `LINK` varchar(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS `ServerList` (
    `SERVER_NAME` varchar(64) NOT NULL UNIQUE,
    `SERVER_ADDRESS` varchar(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS `MonthlyVoteData` (
    `USERNAME` varchar(16) NOT NULL,
    `MONTH` integer NOT NULL,
    `VOTES` integer NOT NULL,
    CONSTRAINT uniqueRelation UNIQUE (`USERNAME`,`MONTH`)
);
