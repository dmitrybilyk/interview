create table accounts (
    name varchar(50) primary key,
    balance bigint not null
);

insert into accounts (name, balance) values ('alice', 1000);
insert into accounts (name, balance) values ('bob', 500);
