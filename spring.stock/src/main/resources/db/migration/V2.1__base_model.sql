-- insert into posts (title, author, content, created_date, modified_date) values ('테스트1', 'test1@gmail.com', '테스트1의 본문', now(), now());
-- insert into posts (title, author, content, created_date, modified_date) values ('테스트2', 'test2@gmail.com', '테스트2의 본문', now(), now());




-- create table IF NOT EXISTS customer (
--     id bigint not null auto_increment, 
--     first_name varchar(255), 
--     last_name varchar(255), 
--     primary key (id)
-- ) engine=InnoDB;

-- create table IF NOT EXISTS  posts (
--     id bigint not null auto_increment, 
--     created_date datetime, 
--     modified_date datetime, 
--     author varchar(255), 
--     content TEXT not null, 
--     title varchar(500) not null,
--     primary key (id)
-- ) engine=InnoDB;



create table comment (id bigint not null auto_increment, created_date datetime, modified_date datetime, content varchar(300), down_vote integer, is_deleted bit, is_modified bit, is_reply bit, reply_parent_id bigint, up_vote integer, member_id bigint, stock_id bigint, primary key (id)) engine=InnoDB;
create table email_token (id varchar(36) not null, expiration_date datetime, type varchar(255), user_id bigint, primary key (id)) engine=InnoDB;
create table hibernate_sequence (next_val bigint) engine=InnoDB;
insert into hibernate_sequence values ( 1 );
create table member (id bigint not null auto_increment, created_date datetime, modified_date datetime, auth_type varchar(50), email varchar(320) not null, email_verified bit, name varchar(30), nickname varchar(20), password varchar(100), picture varchar(255), primary key (id)) engine=InnoDB;
create table member_roles (member_id bigint not null, roles varchar(255)) engine=InnoDB;
create table stock_detail (id bigint not null auto_increment, created_date datetime, modified_date datetime, stock_name varchar(50) not null, ticker_id varchar(30) not null, view_count bigint, primary key (id)) engine=InnoDB;
create table stock_graph (id bigint not null, axes_type varchar(30), axes_value float, date date, stock_id bigint, primary key (id)) engine=InnoDB;
alter table member add constraint UK_mbmcqelty0fbrvxp1q58dn57t unique (email);
alter table stock_detail add constraint UK_q0wvy28njjbm9kiv6xixssmmy unique (ticker_id);
alter table comment add constraint FKmrrrpi513ssu63i2783jyiv9m foreign key (member_id) references member (id);
alter table comment add constraint FKm3da3ttggr6rjwm57lfsej13m foreign key (stock_id) references stock_detail (id);
alter table member_roles add constraint FKet63dfllh4o5qa9qwm7f5kx9x foreign key (member_id) references member (id);
alter table stock_graph add constraint FKqybp1yfqtwdxnnaa62vdngt0o foreign key (stock_id) references stock_detail (id);