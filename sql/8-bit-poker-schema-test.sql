drop database if exists eight_bit_poker_test;
create database eight_bit_poker_test;
use eight_bit_poker_test;

create table player (
	player_id int primary key auto_increment,
    enabled bit not null,
    username varchar(75) not null unique,
    password_hash varchar(2048) not null,
    display_name varchar(100) null,
    account_balance double null,
    roles varchar(50) null,
    hole_cards varchar(150) null,
    position varchar(150) null,
    is_player_action bit null
);

create table board (
	board_id int primary key auto_increment,
    flop varchar(50) null,
    turn varchar(100) null,
    river varchar(200) null
);

create table game (
	game_id int primary key auto_increment,
    pot int null,
    winner varchar(150) null,
    bet_amount double null,
    last_action varchar(50) null,
    board_id int null,
    player_one_id int null,
    player_two_id int null,
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

create table room (
	room_id int primary key auto_increment,
    stake double null,
    seats int null,
    player_count int null,
    deck_id varchar(50) null,
    game_id int null,
	constraint fk_room_game_id
		foreign key (game_id)
        references game(game_id)
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

delimiter //
create procedure set_known_good_state()
begin
	set sql_safe_updates = 0;
    
    
	delete from player_role;
	alter table player_role auto_increment = 1;
	delete from role;
	alter table role auto_increment = 1;
	delete from room;
	alter table room auto_increment = 1;
	delete from game;
	alter table game auto_increment = 1;
	delete from board;
	alter table board auto_increment = 1;
	delete from player;
	alter table player auto_increment = 1;
		
	insert into `role` (`name`) values
		('USER'),
		('ADMIN');
    
	insert into player (player_id, username, password_hash, display_name, account_balance, enabled, roles, hole_cards, `position`, is_player_action)
		values
		(1, 'john@smith.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', null, 110, 1, 'USER', null, null, true),
		(2, 'sally@jones.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', null, 52.75, 1, 'USER', null, null, true),
        (3, 'fred@astair.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', null, 23, 1, 'USER', null, null, false),
        (4, 'billy@bob.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', null, 78, 1, 'ADMIN', null, null, true),
        (5, 'sam@stone.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', null, 154, 1, 'USER', null, null, true),
        (6, 'lisa@simpson.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', null, 205, 1, 'USER', null, null, false);
        
	insert into player_role (player_id, role_id)
		values
        (1, 1),
        (2, 1),
        (3, 1),
        (4, 2),
        (5, 1),
        (6, 1);
		
	insert into board (board_id, flop, turn, river)
		values 
		(1, 'AH,KD,0C', '3S', '9D'),
        (2, '0C,3D,QS', 'AC', '5S'),
        (3, '7H,QS,8S', '6D', '2D');
		
	insert into game (game_id, pot, winner, bet_amount, last_action, board_id, player_one_id, player_two_id)
		values
		(1, 200, 'sally@jones.com', null, 'RAISE', 3, 1, 2),
        (2, 250, 'fred@astair.com', null, 'FOLD', 2, 3, 4),
        (3, 300, 'john@smith.com', null, 'CHECK', 1, 5, 6);
	 
	 insert into room (room_id, stake, seats, deck_id, game_id) 
		values 
		(1, 0.25, 2, 'dflkj43', 2),
        (2, 0.5, 2, null, 3),
        (3, 0.75, 2, null, 1),
        (4, 2.00, 2, null, null),
		(5, 4.00, 2, null, null);
	
    set sql_safe_updates = 1;
    
    end //
delimiter ;