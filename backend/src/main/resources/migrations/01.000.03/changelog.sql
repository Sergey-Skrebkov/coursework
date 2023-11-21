-- liquibase formatted sql

-- changeset skrebkov-ss:4-1

create table outbox_message(
    id uuid primary key,
    message text not null,
    send bool default false,
    accepted bool,
    create_date timestamp default current_timestamp
)