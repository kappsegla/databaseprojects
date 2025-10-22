create database if not exists test;
use test;
drop table if exists country;
create table country
(
    country_id int not null primary key,
    country_name  varchar(255) null,
    language_code varchar(255) null
);

INSERT INTO country (country_id, country_name, language_code)
VALUES (1, 'Sweden', 'sv'),
       (2, 'Norway', 'no'),
       (3, 'Denmark', 'da'),
       (4, 'Germany', 'de');
