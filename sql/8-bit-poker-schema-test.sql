drop database if exists eight_bit_poker_test;
create database eight_bit_poker_test;
use eight_bit_poker_test;

create table player (
	player_id int primary key auto_increment,
    enabled bit not null,
    username varchar(75) not null unique,
    password_hash varchar(2048) not null,
    display_name varchar(100) null,
    account_balance int null,
    roles varchar(50) null,
    hole_cards varchar(150) null,
    position varchar(50) null,
    is_player_action bit null
);

create table room (
	room_id int primary key auto_increment,
    stake double null,
    seats int null
);

create table board (
	board_id int primary key auto_increment,
    flop varchar(50) null,
    turn varchar(100) not null,
    river varchar(200) not null
);

create table game (
	game_id int primary key auto_increment,
    pot int null,
    winner varchar(150) null,
    room_id int not null,
    board_id int not null,
    player_one_id int not null,
    player_two_id int not null,
    constraint fk_game_room_id
		foreign key (room_id)
        references room(room_id),
	constraint fk_game_board_id
		foreign key (board_id)
        references board(board_id),
	constraint fk_game_player_one_id
		foreign key (player_one_id)
        references player(player_id),
	constraint fk_game_player_two_id
		foreign key (player_two_id)
        references player(player_id)
);

create table role (
	role_id int primary key auto_increment,
    `name` varchar(50) not null unique
);

create table player_role (
	player_id int not null,
    role_id int not null,
    constraint pk_player_role
		primary key (player_id, role_id),
	constraint fk_player_role_player_id
		foreign key (player_id)
        references player(player_id),
	constraint fk_player_role_role_id
		foreign key (role_id)
        references role(role_id)
);

insert into role (`name`) values
    ('USER'),
    ('ADMIN');
    
insert into player (player_id, username, password_hash, enabled)
    values
    (1, 'john@smith.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1),
    (2, 'sally@jones.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1);
    

insert into room (room_id, stake, seats) 
	values 
    (1, 0.5, 2);

insert into board (board_id, flop, turn, river)
	values 
    (1, 'AH,KD,10C', '3S', '9D');
    
insert into game (game_id, pot, winner, room_id, board_id, player_one_id, player_two_id)
	values
    (1, 200, null, 1, 1, 1, 2)
