# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table tbAccessory (
  id                        varchar(255) not null,
  name                      varchar(255),
  detail                    varchar(255),
  pic                       varchar(255),
  price                     double,
  amount                    integer,
  type_acc_id               varchar(255),
  constraint pk_tbAccessory primary key (id))
;

create table tbbrands (
  id                        varchar(255) not null,
  name                      varchar(255),
  constraint pk_tbbrands primary key (id))
;

create table tbOrders (
  id                        varchar(255) not null,
  date                      datetime,
  users_username            varchar(255),
  status                    varchar(255),
  constraint pk_tbOrders primary key (id))
;

create table tbOrdersDetail (
  id                        varchar(255) not null,
  orders_id                 varchar(255),
  products_id               varchar(255),
  amount                    integer,
  constraint pk_tbOrdersDetail primary key (id))
;

create table tbproducts (
  id                        varchar(255) not null,
  name                      varchar(255),
  detail                    varchar(255),
  generation                varchar(255),
  pic                       varchar(255),
  price                     double,
  amount                    integer,
  brands_id                 varchar(255),
  types_id                  varchar(255),
  constraint pk_tbproducts primary key (id))
;

create table tbTypeAcc (
  id                        varchar(255) not null,
  name                      varchar(255),
  constraint pk_tbTypeAcc primary key (id))
;

create table tbtypes (
  id                        varchar(255) not null,
  name                      varchar(255),
  constraint pk_tbtypes primary key (id))
;

create table tbuser (
  username                  varchar(255) not null,
  password                  varchar(255),
  name                      varchar(255),
  sername                   varchar(255),
  address                   varchar(255),
  tel                       varchar(255),
  status                    varchar(255),
  constraint pk_tbuser primary key (username))
;

alter table tbAccessory add constraint fk_tbAccessory_typeAcc_1 foreign key (type_acc_id) references tbTypeAcc (id) on delete restrict on update restrict;
create index ix_tbAccessory_typeAcc_1 on tbAccessory (type_acc_id);
alter table tbOrders add constraint fk_tbOrders_users_2 foreign key (users_username) references tbuser (username) on delete restrict on update restrict;
create index ix_tbOrders_users_2 on tbOrders (users_username);
alter table tbOrdersDetail add constraint fk_tbOrdersDetail_orders_3 foreign key (orders_id) references tbOrders (id) on delete restrict on update restrict;
create index ix_tbOrdersDetail_orders_3 on tbOrdersDetail (orders_id);
alter table tbOrdersDetail add constraint fk_tbOrdersDetail_products_4 foreign key (products_id) references tbproducts (id) on delete restrict on update restrict;
create index ix_tbOrdersDetail_products_4 on tbOrdersDetail (products_id);
alter table tbproducts add constraint fk_tbproducts_brands_5 foreign key (brands_id) references tbbrands (id) on delete restrict on update restrict;
create index ix_tbproducts_brands_5 on tbproducts (brands_id);
alter table tbproducts add constraint fk_tbproducts_types_6 foreign key (types_id) references tbtypes (id) on delete restrict on update restrict;
create index ix_tbproducts_types_6 on tbproducts (types_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table tbAccessory;

drop table tbbrands;

drop table tbOrders;

drop table tbOrdersDetail;

drop table tbproducts;

drop table tbTypeAcc;

drop table tbtypes;

drop table tbuser;

SET FOREIGN_KEY_CHECKS=1;

