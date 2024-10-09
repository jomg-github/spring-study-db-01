create table if not exists tb_member
(
    member_id int not null auto_increment,
    money     int not null,
    primary key (member_id)
);

insert into tb_member (money) values (10000), (20000);