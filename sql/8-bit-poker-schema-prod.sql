drop database if exists eight_bit_poker;
create database eight_bit_poker;
use eight_bit_poker;

create table player (
	player_id int primary key auto_increment,
    username varchar(75) not null unique,
    password_hash varchar(2048) not null,
    display_name varchar(100) not null,
    account_balance int null,
    roles varchar(50) not null,
    hole_cards varchar(150) null,
    position varchar(50) not null,
    is_player_action bit not null
);