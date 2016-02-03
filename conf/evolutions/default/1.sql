# --- !Ups

create table note (
  id                        bigint not null primary key,
  title                     varchar(255) not null,
  body                      varchar(255) not null
);

create sequence note_seq start with 1000;

# --- !Downs

drop table if exists note;
drop sequence if exists note_seq;
