-- liquibase formatted sql

-- changeset skrebkov-ss:3-1

create table message_for_kafka(
    id bigserial primary key,
    message text not null,
    send bool default false,
    accepted bool,
    create_date timestamp default current_timestamp
)