-- liquibase formatted sql

-- changeset skrebkov-ss:3-1

create table message_for_kafka(
    id uuid primary key default gen_random_uuid (),
    message text not null,
    send bool default false,
    accepted bool,
    create_date timestamp default current_timestamp
)