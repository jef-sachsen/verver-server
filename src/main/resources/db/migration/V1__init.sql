-- ACL TABLES
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

-- MV TABLES
CREATE TABLE cm_address (
  id       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  street   VARCHAR(255),
  zip_code BIGINT(255),
  city     VARCHAR(255),
  country  VARCHAR(255)
);

CREATE TABLE cm_voluntarydetails (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  profession VARCHAR(255)
);

CREATE TABLE cm_bankdetails (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  account_owner VARCHAR(255),
  iban          VARCHAR(255)
);

CREATE TABLE cm_contacts (
  id                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email                VARCHAR(255) UNIQUE,
  first_name           VARCHAR(255),
  last_name            VARCHAR(255),
  phone                VARCHAR(255),
  date_of_birth        TIMESTAMP,
  address_id           BIGINT UNSIGNED NOT NULL REFERENCES cm_address (id),
  bank_account_id      BIGINT UNSIGNED NOT NULL REFERENCES cm_bankdetails (id),
  voluntary_details_id BIGINT UNSIGNED REFERENCES cm_voluntarydetails (id)
);

CREATE TABLE mv_cm_groups (
  id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name            VARCHAR(255) UNIQUE,
  acl_sid_id      BIGINT UNIQUE REFERENCES acl_sid (id),
  permission_enum INTEGER
);

CREATE TABLE mv_users (
  id                       BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT PRIMARY KEY,
  enabled                  BOOLEAN             NOT NULL DEFAULT FALSE,
  account_non_expired      BOOLEAN                      DEFAULT TRUE,
  account_non_locked       BOOLEAN                      DEFAULT TRUE,
  credentials_non_expired  BOOLEAN                      DEFAULT TRUE,
  admin                    BOOLEAN                      DEFAULT FALSE,
  last_password_reset_date TIMESTAMP,
  username                 VARCHAR(255) UNIQUE NOT NULL,
  password                 VARCHAR(255),
  contact_id               BIGINT UNIQUE REFERENCES cm_contacts (id)
);

CREATE TABLE mv_users_email_verification_token (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT UNSIGNED NOT NULL REFERENCES mv_users (id),
  token      VARCHAR(255),
  issue_date TIMESTAMP
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

-- JOIN TABLES
CREATE TABLE mv_user_group (
  user_id  BIGINT NOT NULL REFERENCES mv_users (id)
    ON DELETE CASCADE,
  group_id BIGINT NOT NULL REFERENCES mv_cm_groups (id)
    ON DELETE CASCADE
);

CREATE TABLE mv_role_authority (
  role_id      BIGINT NOT NULL REFERENCES mv_roles (id)
    ON DELETE CASCADE,
  authority_id BIGINT NOT NULL REFERENCES mv_authorities (id)
    ON DELETE CASCADE
);

CREATE TABLE mv_user_role (
  user_id BIGINT NOT NULL REFERENCES mv_users (id)
    ON DELETE CASCADE,
  role_id BIGINT NOT NULL REFERENCES mv_roles (id)
    ON DELETE CASCADE
);

CREATE TABLE mv_group_contact (
  group_id   BIGINT NOT NULL REFERENCES mv_cm_groups (id)
    ON DELETE CASCADE,
  contact_id BIGINT NOT NULL REFERENCES cm_contacts (id)
    ON DELETE CASCADE
);

CREATE TABLE mv_group_contact_responsible (
  group_id   BIGINT NOT NULL REFERENCES mv_cm_groups (id)
    ON DELETE CASCADE,
  contact_id BIGINT NOT NULL REFERENCES cm_contacts (id)
    ON DELETE CASCADE
);

INSERT INTO acl_sid (principal, sid)
VALUES (FALSE, 'ROLE_ADMIN'),
       (FALSE, 'ROLE_USER');

INSERT INTO mv_authorities (name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_USER'),
    -- SYSTEM AUTHORITIES
    -- USER
       ('SYS_USER_GETALL'),
       ('SYS_USER_GETUSERBYID'),
       ('SYS_USER_GETUSERSBYIDS'),
       ('SYS_USER_GETUSERBYUSERNAME'),
       ('SYS_USER_GETALLBYROLE'),
       ('SYS_USER_GETALLBYGROUP'),
       ('SYS_USER_CREATE'),
       ('SYS_USER_UPDATE'),
       ('SYS_USER_DELETE'),
    -- ROLE
       ('SYS_ROLE_CREATE'),
       ('SYS_ROLE_GETROLEBYID'),
       ('SYS_ROLE_GETROLESBYIDS'),
       ('SYS_ROLE_GETALL'),
       ('SYS_ROLE_FINDUSERSINROLE'),
       ('SYS_ROLE_UPDATE'),
       ('SYS_ROLE_DELETE'),
       ('SYS_ROLE_ADDUSERTOROLE'),
       ('SYS_ROLE_ADDUSERSTOROLE'),
       ('SYS_ROLE_REMOVEUSERFROMROLE'),
       ('SYS_ROLE_REMOVEUSERSFROMROLE'),
       ('SYS_ROLE_REMOVEALLUSERSFROMROLE'),
       ('SYS_ROLE_ADDAUTHORITYTOROLE'),
       ('SYS_ROLE_ADDAUTHORITIESTOROLE'),
       ('SYS_ROLE_REMOVEAUTHORITYFROMROLE'),
       ('SYS_ROLE_REMOVEAUTHORITIESFROMROLE'),
    -- CONTACTMANAGEMENT AUTHORITIES
    -- CONTACT
       ('CM_CONTACT_CREATE'),
       ('CM_CONTACT_GETCONTACTBYID'),
       ('CM_CONTACT_UPDATE'),
       ('CM_CONTACT_DELETE'),
       ('CM_CONTACT_ADDPERMISSIONTOGROUP'),
       ('CM_CONTACT_ADDPERMISSIONTOGROUPS'),
       ('CM_CONTACT_GETALL'),
       ('CM_CONTACT_GETCONTACTSBYIDS'),
    -- GROUP
       ('CM_GROUP_CREATE'),
       ('CM_GROUP_GETGROUPBYID'),
       ('CM_GROUP_GETGROUPSBYIDS'),
       ('CM_GROUP_GETALL'),
       ('CM_GROUP_GETALLBYCONTACTS'),
       ('CM_GROUP_FINDUSERSINGROUP'),
       ('CM_GROUP_UPDATE'),
       ('CM_GROUP_DELETE'),
       ('CM_GROUP_ADDUSERTOGROUP'),
       ('CM_GROUP_ADDUSERSTOGROUP'),
       ('CM_GROUP_REMOVEUSERFROMGROUP'),
       ('CM_GROUP_REMOVEUSERSFROMGROUP'),
       ('CM_GROUP_ADDAUTHORITYTOGROUP'),
       ('CM_GROUP_ADDAUTHORITIESTOGROUP'),
       ('CM_GROUP_REMOVEAUTHORITYFROMGROUP'),
       ('CM_GROUP_REMOVEAUTHORITIESFROMGROUP');

-- Create one user object as the first user, called admin
INSERT INTO mv_users (admin, enabled, account_non_expired, account_non_locked, credentials_non_expired, username, last_password_reset_date, password)
VALUES
  (TRUE, TRUE, TRUE, TRUE, TRUE, 'admin@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, 'user@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, FALSE, TRUE, TRUE, TRUE, 'disabled@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, 'admin2@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (TRUE, TRUE, TRUE, TRUE, TRUE, '1admin@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '1user@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, FALSE, TRUE, TRUE, TRUE, '1disabled@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '1admin2@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (TRUE, TRUE, TRUE, TRUE, TRUE, '2admin@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '2user@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, FALSE, TRUE, TRUE, TRUE, '2disabled@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '2admin2@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (TRUE, TRUE, TRUE, TRUE, TRUE, '3admin@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '3user@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, FALSE, TRUE, TRUE, TRUE, '3disabled@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '3admin2@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (TRUE, TRUE, TRUE, TRUE, TRUE, '4admin@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '4user@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, FALSE, TRUE, TRUE, TRUE, '4disabled@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '4admin2@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (TRUE, TRUE, TRUE, TRUE, TRUE, '5admin@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '5user@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, FALSE, TRUE, TRUE, TRUE, '5disabled@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '5admin2@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (TRUE, TRUE, TRUE, TRUE, TRUE, '6admin@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '6user@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, FALSE, TRUE, TRUE, TRUE, '6disabled@test.com', current_date,
   '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC'),
  (FALSE, TRUE, TRUE, TRUE, TRUE, '6admin2@test.com', current_date,
   '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi');