CREATE TABLE acl_sid (
  id        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  principal BOOLEAN         NOT NULL,
  sid       VARCHAR(100)    NOT NULL,
  UNIQUE KEY unique_acl_sid (sid, principal)
)
  ENGINE = InnoDB;

CREATE TABLE acl_class (
  id    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  class VARCHAR(100)    NOT NULL,
  UNIQUE KEY uk_acl_class (CLASS)
)
  ENGINE = InnoDB;

CREATE TABLE acl_object_identity (
  id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  object_id_class    BIGINT UNSIGNED NOT NULL,
  object_id_identity VARCHAR(36)     NOT NULL,
  parent_object      BIGINT UNSIGNED,
  owner_sid          BIGINT UNSIGNED,
  entries_inheriting BOOLEAN         NOT NULL,
  UNIQUE KEY uk_acl_object_identity (object_id_class, object_id_identity),
  CONSTRAINT fk_acl_object_identity_parent FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id),
  CONSTRAINT fk_acl_object_identity_class FOREIGN KEY (object_id_class) REFERENCES acl_class (id),
  CONSTRAINT fk_acl_object_identity_owner FOREIGN KEY (owner_sid) REFERENCES acl_sid (id)
)
  ENGINE = InnoDB;

CREATE TABLE acl_entry (
  id                  BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
  acl_object_identity BIGINT UNSIGNED  NOT NULL,
  ace_order           INTEGER          NOT NULL,
  sid                 BIGINT UNSIGNED  NOT NULL,
  mask                INTEGER UNSIGNED NOT NULL,
  granting            BOOLEAN          NOT NULL,
  audit_success       BOOLEAN          NOT NULL,
  audit_failure       BOOLEAN          NOT NULL,
  UNIQUE KEY unique_acl_entry (acl_object_identity, ace_order),
  CONSTRAINT fk_acl_entry_object FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id),
  CONSTRAINT fk_acl_entry_acl FOREIGN KEY (sid) REFERENCES acl_sid (id)
    ON DELETE CASCADE
)
  ENGINE = InnoDB;

CREATE TABLE mv_cm_contacts (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email        VARCHAR(255) UNIQUE,
  first_name   VARCHAR(255),
  last_name    VARCHAR(255),
  phone        VARCHAR(255),
  address      VARCHAR(255),
  bank_details VARCHAR(255)
);

CREATE TABLE mv_cm_groups (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(255) UNIQUE,
  acl_sid_id BIGINT UNIQUE REFERENCES acl_sid (id)
);

CREATE TABLE mv_users (
  id                       BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT PRIMARY KEY,
  enabled                  BOOLEAN             NOT NULL DEFAULT FALSE,
  account_non_expired      BOOLEAN                      DEFAULT TRUE,
  account_non_locked       BOOLEAN                      DEFAULT TRUE,
  credentials_non_expired  BOOLEAN                      DEFAULT TRUE,
  is_admin                 BOOLEAN                      DEFAULT FALSE,
  last_password_reset_date TIMESTAMP,
  username                 VARCHAR(255) UNIQUE NOT NULL,
  password                 VARCHAR(255),
  contact_id               BIGINT UNIQUE REFERENCES mv_cm_contacts (id)
);

CREATE TABLE mv_authorities (
  id               BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name             VARCHAR(100) UNIQUE NOT NULL,
  system_authority BOOLEAN
);

CREATE TABLE mv_roles (
  id   BIGINT UNSIGNED    NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE mv_user_group (
  user_id BIGINT NOT NULL REFERENCES mv_users (id),
  group_id BIGINT NOT NULL REFERENCES mv_cm_groups (id)
);

CREATE TABLE mv_authority_role (
  authority_id BIGINT NOT NULL REFERENCES mv_authorities (id),
  role_id      BIGINT NOT NULL REFERENCES mv_roles (id)
);

CREATE TABLE mv_user_role (
  user_id BIGINT NOT NULL REFERENCES mv_users (id),
  role_id BIGINT NOT NULL REFERENCES mv_roles (id)
);

INSERT INTO acl_sid (principal, sid) VALUES
  (FALSE, 'ROLE_ADMIN'),
  (FALSE, 'ROLE_USER');

INSERT INTO mv_authorities (name) VALUES
  ('ROLE_ADMIN'), ('ROLE_USER'), ('CM_CONTACT_CREATE'), ('CM_CONTACT_GET_BY_ID');

-- Create one user object as the first user, called admin
INSERT INTO mv_users (is_admin, enabled, account_non_expired, account_non_locked, credentials_non_expired, username, last_password_reset_date, password)
VALUES
  (TRUE, TRUE, TRUE, TRUE, TRUE, 'admin', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, 'user', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, FALSE, TRUE, TRUE, TRUE, 'disabled', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (TRUE, TRUE, TRUE, TRUE, TRUE, 'admin2', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi');

-- Create Role Admins
INSERT INTO mv_roles (name) VALUES ('Administrators');

-- Add user 1 admin to Admin role
INSERT INTO mv_user_role (user_id, role_id) VALUES (1, 1);

-- Give Admin role ROLE_ADMIN and other permissions
INSERT INTO mv_authority_role (authority_id, role_id) VALUES (1, 1), (3, 1);