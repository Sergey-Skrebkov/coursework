-- liquibase formatted sql

-- changeset skrebkov-ss:1-1
create table status
(
    id   bigserial primary key,
    code text not null unique,
    name text not null
);

create table document
(
    id           bigserial primary key,
    type         text not null,
    organization text not null,
    date         timestamp,
    patient      text not null,
    description  text,
    status_id    bigint
        references status
);

-- changeset skrebkov-ss:1-2

insert into status(id, code, name)
values (1, 'NEW', 'Новый'),
       (2, 'IN_PROCESS', 'В обработке'),
       (3, 'ACCEPTED', 'Принят'),
       (4, 'REJECTED', 'Отклонен');