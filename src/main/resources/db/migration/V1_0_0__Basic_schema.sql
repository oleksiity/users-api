create schema if not exists user_management;

create table user_management.user
(
    id        serial primary key,
    email varchar(30) not null check (length(trim(email)) > 0) unique,
    first_name varchar(20) not null check (length(trim(first_name)) > 0),
    last_name varchar(20) not null check (length(trim(last_name)) > 0),
    birth_date date not null,
    address varchar(150),
    phone_number varchar(10) unique
);