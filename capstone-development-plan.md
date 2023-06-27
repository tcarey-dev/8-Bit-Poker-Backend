# Development Plan

## Team Name: Code Counters

## User Stories

* Player User Story
    * As a player, I want to be able to create an account, deposit (fake) money, choose a Room, and play a game.

* Admin User Story
    * As admin, I want to be able to create new Rooms, update existing Room settings (such as stakes), and delete Rooms

## Models
* Player
  * int id
  * String displayName
  * String username
  * String password
  * int accountBalance
  * Collection <GrantedAuthorities> roles
  * List<Card> holeCards
  * Position position
  * boolean isPlayersAction

* Room
  * int id
  * double stake
  * int[] seats

* Game
  * int id
  * Room room
  * int pot
  * Board board
  * List<Player> players
  * Player winner

* Board
  * int id
  * List<Card> flop
  * Card turn
  * Card river

* Position (enum)
  * SMALL_BLIND
  * BIG_BLIND

* Action (enum)
  * BET
  * CHECK
  * FOLD
  * RAISE

* Card (enum)

## Validation rules/business logic
* Room stake must be even and greater or equal to 2 cents (consider creating drop down for stakes)

## Package/Class Overview
```
src
├───main
│   ├───java
│   │   └───learn
│   │       └───poker
│   │           │   App.java
│   │           │   AppConfig.java
│   │           │   WebSocketConfig.java
│   │           │
│   │           ├───security
│   │           │       Credentials.java
│   │           │       JwtConverter.java
│   │           │       JwtRequestFilter.java
│   │           │       SecurityConfig.java
│   │           │
│   │           ├───data
│   │           |   └─── mappers
|   |           |       |      PlayerMapper.java
|   |           |       |      RoomMapper.java
|   |           |       |      GameMapper.java
│   │           │       DataException.java
│   │           │       RoomJdbcTemplateRepository.java
│   │           │       RoomRepository.java
│   │           │       PlayerJdbcTemplateRepository.java
│   │           │       PlayerRepository.java
│   │           │       GameJdbcTemplateRepository.java
│   │           │       GameRepository.java
│   │           │
│   │           ├───domain
│   │           │       PlayerService.java
│   │           │       RoomService.java
│   │           │       GameService.java
│   │           │       ResultType.java
│   │           │       Result.java
│   │           │
│   │           ├───models
│   │           │       Player.java
│   │           │       Room.java
│   │           │       Game.java
│   │           │       Board.java
│   │           │       Position.java
│   │           │       Action.java
│   │           │
│   │           └───controller
│   │                   PlayerController.java
│   │                   RoomController.java
│   │                   GameController.java
│   │                   AuthController.java
│   │                   GlobalExceptionHandler.java
│   │
│   └───resources
└───test
    └───java
        └───learn
            └───mastery
                ├───data
                │       UserRepositoryTest.java
                │       UserRepositoryDouble.java
                │       RoomRepositoryTest.java
                │       RoomRepositoryDouble.java
                │       GameRepositoryTest.java
                │       GameRepositoryDouble.java
                │
                └───domain
                        UserServiceTest.java
                        RoomServiceTest.java
                        GameServiceTest.java
```

## Class methods and fields

#### src.main.java.learn.poker
* [ ] `App.java` -- @SpringBootApplication
* [ ] `AppConfig.java` -- @Configuration
    * `public PasswordEncoder getPasswordEncoder()` -- @Bean
    * `public WebMvcConfigurer corsConfigurer()` -- @Bean
* [ ] `WebSocketConfig.java` -- @Configuration, @EnableWebSocketMessageBroker
    * `public void configureMessageBroker(MessageBrokerRegistry config)` -- @Override
    * `public void registerStompEndpoints(StompEndpointRegistry registry)` --@Override

#### src.main.java.learn.poker.security
* [ ] `Credentials.java` 
    * `private String username`
    * `private String password`
    * constructor, getters and setters
* [ ] `JwtConverter.java` 
    * `private Key key`
    * `private final String ISSUER`
    * `private final int EXPIRATION_MINUTES`
    * `private final int EXPIRATION_MILLIS`
    * `public String getTokenFromPlayer(Player user)`
    * `public Player getPlayerFromToken(String token)`
* [ ] `JwtRequestFilter.java` -- extends BasicAuthenticationFilter
    * `private final JwtConverter converter`
    * `public JwtRequestFilter(AuthenticationManager authenticationManager, JwtConverter converter)`
    * `protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException ` -- @Override
* [ ] `SecurityConfig.java` -- @Configuration, @ConditionalOnWebApplication
    * `private final JwtConverter jwtConverter`
    * `public SecurityConfig(JwtConverter jwtConverter)`
    * `public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception` -- @Bean
    * `public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception` -- @Bean

#### src.main.java.learn.poker.data

* [ ] `DataException.java` 
    * `public DataException(String, Throwable)`
* [ ] `RoomJdbcTemplateRepository.java` 
    * `private final JdbcTemplate jdbcTemplate`
    * `private final RowMapper<Room> rowMapper = new RoomMapper()`
    * `public RoomJdbcTemplateRepository(JdbcTemplate jdbcTemplate)`
    * `public List<Room> findAll()` -- @Override
    * `public Room findById()` -- @Override
    * `public Room create(Room)` -- @Override
    * `public boolean update(Room)` -- @Override
    * `public boolean delete(Room)` -- @Override
* [ ] `RoomRepository.java` 
    * extract interface from RoomJdbcTemplateRepository
* [ ] `PlayerJdbcTemplateRepository.java` 
    * `private final JdbcTemplate jdbcTemplate`
    * `public PlayerJdbcTemplateRepository(JdbcTemplate jdbcTemplate)`
    * `public Player findByUsername(String username)` -- @Override
    * `public Player create(Player player)` -- @Override
    * `public boolean update(Player player)` -- @Override
    * `public boolean delete(Player player)` -- @Override 
    * `private void updateRoles(Player player)`
    * `private List<String> getRolesByUsername(String username)`
* [ ] `PlayerRepository.java` 
    * extract interface from PlayerJdbcTemplateRepository
* [ ] `GameJdbcTemplateRepository.java` 
    * `private final JdbcTemplate jdbcTemplate`
    * `private final RowMapper<Game> rowMapper = new GameMapper()`
    * `public GameJdbcTemplateRepository(JdbcTemplate jdbcTemplate)`
    * `public Game findById(int gameId)` -- @Override
    * `public Game create(Game)` -- @Override
    * `public boolean update(Game)` -- @Override
    * `public boolean delete(Game)` -- @Override
    * we might need methods to get the room, board and list of players from the Game
* [ ] `GameRepository.java` 
    * extract interface from GameJdbcTemplateRepository

#### src.main.java.learn.poker.data.mappers
* [ ] `PlayerMapper.java` -- implements RowMapper<Player>
    * `private final List<String> roles`
    * `public PlayerMapper(List<String> roles)`
    * `public Player mapRow(ResultSet rs, int i) throws SQLException` -- @Override
* [ ] `RoomMapper.java` -- implements RowMapper<Room>
    * `public Room mapRow(ResultSet rs, int rowNum) throws SQLException` -- @Override
* [ ] `GameMapper.java` -- implements RowMapper<Game>
    * `public Game mapRow(ResultSet rs, int rowNum) throws SQLException` -- @Override

#### src.main.java.learn.poker.domain
* [ ] `PlayerService.java` 
    * `private PlayerRepository repository`
    * `public Player(PlayerRepository)`
    * `public Player getPlayerById(int)`
    * `public Result add(Player)`
    * `public Result update(Player)`
    * `public Result delete(Player)`
    * `private Result validate(Player)`
* [ ] `RoomService.java` 
    * `private final RoomRepository repository`
    * ` public RoomService(RoomRepository repository)
    * `public List<Room> findAll()`
    * `public Room findById(int)`
    * `public Result<Room> add(Room)`
    * `public Result<Room> update(Room)`
    * `public boolean deleteById(int)`
    * `private Result<Room> validate(Room)`
* [ ] `GameService.java` 
    * `private final GameRepository repository`
    * `public GameService(GameRepository repository)`
    * `public List<Game> findAll()`
    * `public Game findById(int)`
    * `public Player getWinner(List<Player> players, Board board)`
    * `public Result<Game> add(Game)`
    * `public Result<Game> update(Game)`
    * `public boolean deleteById(int)`
    * `private Result<Game> validate(Game)`
    * add game init and loop methods here, along with any helper methods
    * we might need methods to get the room, board and list of players from the Game
* [ ] `ResultType.java`  -- Enum
    * SUCCESS,INVALID,NOT_FOUND
* [ ] `Result.java` 
    * `private final ArrayList<String> messages`
    * `private ResultType type`
    * `private T payload`
    * `public boolean isSuccess()`
    * get and set payload, get type, get and add message

#### src.main.java.learn.poker.models
* [ ] `Player.java` -- implements UserDetails
    * `int id`
    * `private String displayName`
    * `private final String username`
    * `private final String password`
    * `int accountBalance`
    * `Collection <GrantedAuthorities> roles`
    * `List<Card> holeCards`
    * `Position position`
    * `boolean isPlayersAction`
    * `public Player(int playerId, String username, String password, List<String> roles)`
    * constructor, getters and setters, as well as override methods to implement UserDetails contract
* [ ] `Room.java` 
    * `int roomId`
    * `double stake`
    * `int seats`
* [ ] `Game.java` 
    * `int gameId`
    * `int pot`
    * `String winner`
    * `Room room`
    * `Board board`
    * `Player player`
* [ ] `Board.java` 
    * `int boardId`
    * `List<Card> flop`
    * `Card turn`
    * `Card river`
* [ ] `Position.java` -- Enum
    * SMALL_BLIND, BIG_BLIND
* [ ] `Action.java` -- Enum
    * BET, CHECK, FOLD, RAISE
* [ ] `Card.java` -- Enum
    * 2C, 3C, 4C, 5C, 6C, 7C, 8C, 9C, 10C, JC, QC, KC, AC, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 10D, JD, QD, KD, AD, 2H, 3H, 4H, 5H, 6H, 7H, 8H, 9H, 10H, JH, QH, KH, AH, 2S, 3S, 4S, 5S, 6S, 7S, 8S, 9S, 10S, JS, QS, KS, AS

#### src.main.java.learn.poker.controller
* [ ] `PlayerController.java` 
    * 
* [ ] `RoomController.java` 
* [ ] `GameController.java` 
* [ ] `AuthController.java` 
* [ ] `GlobalExceptionHandler.java` 

### test.java.learn.mastery.data
* [ ] `UserRepositoryTest.java` 
* [ ] `UserRepositoryDouble.java` 
* [ ] `RoomRepositoryTest.java` 
* [ ] `RoomRepositoryDouble.java` 
* [ ] `GameRepositoryTest.java` 
* [ ] `GameRepositoryDouble.java` 

### test.java.learn.mastery.domain
* [ ] `UserServiceTest.java` 
* [ ] `RoomServiceTest.java` 
* [ ] `GameServiceTest.java` 


GAME LOGIC
 * init game 
    * initialize the game, room, players (i.e. set initial state) -- Database call
    * return game state to all subscibers -- Push to UI
    * trigger game loop
* game loop
    * get deck of cards, deal cards to players -- API call, push to UI
    * do small blinds action
    * do big blinds action 
    * if action is ending (meaning the player who is last flat calls, or checks) then deal next street (flop, turn, river) 
    * if action is fold, restart game loop



