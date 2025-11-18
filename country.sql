create database if not exists test;
use test;

drop table if exists country;

create table country
(
    country_id    int          not null auto_increment primary key,
    country_name  varchar(255) null,
    language_code varchar(255) null,
    last_updated  timestamp default current_timestamp on update current_timestamp
);

INSERT INTO country (country_name, language_code, last_updated)
VALUES ('Sweden', 'sv', NOW()),
       ('Norway', 'no', NOW()),
       ('Denmark', 'da', NOW()),
       ('Germany', 'de', NOW());
