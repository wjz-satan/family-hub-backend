CREATE DATABASE IF NOT EXISTS `familyhub`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `familyhub`;

CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `phone` VARCHAR(11) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `openid` VARCHAR(64) NULL,
  `nickname` VARCHAR(50) NOT NULL,
  `avatar_url` VARCHAR(500) NULL,
  `enabled` BIT(1) NOT NULL DEFAULT b'1',
  `created_at` DATETIME(6) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_phone` (`phone`),
  UNIQUE KEY `uk_users_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `families` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `avatar_url` VARCHAR(500) NULL,
  `invite_code` VARCHAR(6) NULL,
  `announcement` VARCHAR(500) NULL,
  `owner_id` BIGINT NOT NULL,
  `active` BIT(1) NOT NULL DEFAULT b'1',
  `created_at` DATETIME(6) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_families_invite_code` (`invite_code`),
  KEY `idx_families_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `family_members` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `family_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `role` VARCHAR(20) NOT NULL,
  `alias` VARCHAR(20) NULL,
  `joined_at` DATETIME(6) NOT NULL,
  `active` BIT(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_family_members_family_user` (`family_id`, `user_id`),
  KEY `idx_family_members_user_id` (`user_id`),
  CONSTRAINT `fk_family_members_family` FOREIGN KEY (`family_id`) REFERENCES `families` (`id`),
  CONSTRAINT `fk_family_members_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `tasks` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `family_id` BIGINT NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `description` VARCHAR(500) NULL,
  `priority` VARCHAR(20) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `creator_id` BIGINT NOT NULL,
  `due_date` DATETIME(6) NULL,
  `repeat_rule` VARCHAR(100) NULL,
  `reward_points` INT NOT NULL DEFAULT 0,
  `completed_at` DATETIME(6) NULL,
  `created_at` DATETIME(6) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tasks_family_id` (`family_id`),
  KEY `idx_tasks_creator_id` (`creator_id`),
  KEY `idx_tasks_status` (`status`),
  CONSTRAINT `fk_tasks_family` FOREIGN KEY (`family_id`) REFERENCES `families` (`id`),
  CONSTRAINT `fk_tasks_creator` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `task_assignees` (
  `task_id` BIGINT NOT NULL,
  `user_id` BIGINT NULL,
  KEY `idx_task_assignees_task_id` (`task_id`),
  KEY `idx_task_assignees_user_id` (`user_id`),
  CONSTRAINT `fk_task_assignees_task` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_task_assignees_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `task_tags` (
  `task_id` BIGINT NOT NULL,
  `tag_value` VARCHAR(255) NULL,
  KEY `idx_task_tags_task_id` (`task_id`),
  CONSTRAINT `fk_task_tags_task` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `calendar_events` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `family_id` BIGINT NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `description` VARCHAR(500) NULL,
  `location` VARCHAR(200) NULL,
  `start_time` DATETIME(6) NOT NULL,
  `end_time` DATETIME(6) NOT NULL,
  `all_day` BIT(1) NOT NULL DEFAULT b'0',
  `color` VARCHAR(7) NULL,
  `creator_id` BIGINT NOT NULL,
  `remind_before_minutes` INT NULL,
  `repeat_rule` VARCHAR(100) NULL,
  `created_at` DATETIME(6) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_calendar_events_family_id` (`family_id`),
  KEY `idx_calendar_events_creator_id` (`creator_id`),
  KEY `idx_calendar_events_start_time` (`start_time`),
  CONSTRAINT `fk_calendar_events_family` FOREIGN KEY (`family_id`) REFERENCES `families` (`id`),
  CONSTRAINT `fk_calendar_events_creator` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `calendar_event_participants` (
  `event_id` BIGINT NOT NULL,
  `user_id` BIGINT NULL,
  KEY `idx_calendar_event_participants_event_id` (`event_id`),
  KEY `idx_calendar_event_participants_user_id` (`user_id`),
  CONSTRAINT `fk_calendar_event_participants_event` FOREIGN KEY (`event_id`) REFERENCES `calendar_events` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_calendar_event_participants_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `bills` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `family_id` BIGINT NOT NULL,
  `member_id` BIGINT NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `category` VARCHAR(50) NOT NULL,
  `payment_method` VARCHAR(20) NOT NULL,
  `bill_date` DATE NOT NULL,
  `note` VARCHAR(200) NULL,
  `created_at` DATETIME(6) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_bills_family_id` (`family_id`),
  KEY `idx_bills_member_id` (`member_id`),
  KEY `idx_bills_bill_date` (`bill_date`),
  CONSTRAINT `fk_bills_family` FOREIGN KEY (`family_id`) REFERENCES `families` (`id`),
  CONSTRAINT `fk_bills_member` FOREIGN KEY (`member_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
