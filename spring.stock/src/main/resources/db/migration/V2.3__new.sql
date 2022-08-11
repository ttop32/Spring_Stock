
-- drop table if exists customer;
-- drop table if exists email_token;
-- drop table if exists member;
-- drop table if exists member_entity_roles;
-- drop table if exists posts;


-- create table customer (id bigint not null auto_increment, first_name varchar(255), last_name varchar(255), primary key (id)) engine=InnoDB;
-- create table email_token (id varchar(36) not null, expiration_date datetime, user_id bigint, primary key (id)) engine=InnoDB;
-- create table member (id bigint not null auto_increment, email varchar(320) not null, email_verified bit, name varchar(255), password varchar(100), picture varchar(255), primary key (id)) engine=InnoDB;
-- create table member_entity_roles (member_entity_id bigint not null, roles varchar(255)) engine=InnoDB;
-- create table posts (id bigint not null auto_increment, created_date datetime, modified_date datetime, author varchar(255), content TEXT not null, title varchar(500) not null, primary key (id)) engine=InnoDB;
-- alter table member_entity_roles add constraint FK4a7caewa5wt0h9jr30sn4p5vm foreign key (member_entity_id) references member (id);


