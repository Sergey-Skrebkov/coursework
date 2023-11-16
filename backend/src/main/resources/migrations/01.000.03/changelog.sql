-- liquibase formatted sql

-- changeset skrebkov-ss:3-1

create table message_for_kafka_answer(
    id uuid primary key,
    message text not null,
    send bool default false,
    accepted bool,
    create_date timestamp default current_timestamp
)